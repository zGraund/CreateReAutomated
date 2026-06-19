package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.api.DrillPartialIndex;
import com.github.zgraund.createreautomated.api.block.Extractable;
import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.config.RecipeModifiers;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipeInput;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.github.zgraund.createreautomated.registry.ModTags;
import com.github.zgraund.createreautomated.util.CreateRALang;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtractorBlockEntity extends KineticBlockEntity {
    public static final float DEFAULT_DRILL_OFFSET = 0.85f;
    public static final float RETRACTED_DRILL_OFFSET = 0.55f;

    protected final ItemStackHandler drillInv = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }
    };
    protected final ItemStackHandler outputInv = new ItemStackHandler(6);
    protected final IItemHandler capabilities = new ExtractorInventoryHandler();
    protected int progress;
    protected DrillingBehaviour animation;
    @Nullable
    protected ExtractingRecipe recipe;

    public ExtractorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static <T extends AbstractExtractorBlock<?>> void registerInventoryCapabilities(RegisterCapabilitiesEvent event, NonNullSupplier<T> block) {
        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    if (AbstractExtractorBlock.isLower(state)) {
                        pos = pos.above();
                        state = level.getBlockState(pos);
                        blockEntity = level.getBlockEntity(pos);
                        // in case I need to change the capability based on direction
                        // context = Direction.DOWN;
                    }
                    if (AbstractExtractorBlock.isUpper(state) && blockEntity instanceof ExtractorBlockEntity extractor)
                        return extractor.capabilities;
                    return null;
                },
                block.get()
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new DirectBeltInputBehaviour(this).considerOccupiedWhen(d -> hasDrill()));
        animation = new DrillingBehaviour(this).startAt(RETRACTED_DRILL_OFFSET).to(this::getNodeDrillOffset);
        behaviours.add(animation);
    }

    @Override
    public void tick() {
        if (level == null) return;

        BlockPos nodePos = getNodePosition();
        if (failPreConditions()) {
            resetRecipe();
        } else {
            ExtractingRecipeInput input = ExtractingRecipeInput.of(this);
            if (recipe == null || !recipe.matches(input, level)) {
                getRecipeFor(input).ifPresentOrElse(this::setRecipe, this::resetRecipe);
            }
        }

        super.tick();

        if (level.isClientSide()) {
            spawnParticles();
            return;
        }

        if (!isExtracting() || recipe == null)
            return;

        tickProgress();

        if (progress >= recipe.getProcessingDurationModified()) {
            if (getNode().getBlock() instanceof Extractable node) {
                node.extract(recipe.getExtractionQuantityModified(), nodePos, level);
            }
            recipe.rollResultsModified(level.random).forEach(result ->
                    ItemHandlerHelper.insertItemStacked(outputInv, result, false)
            );
            if (Config.server().extractorConfig.useDrillDurability.get()) {
                getDrill().hurtAndBreak(recipe.getDurabilityCostModified(), (ServerLevel) level, null, this::onDrillBreak);
            }
            progress = 0;
        }

        notifyUpdate();
    }

    public void tickProgress() {
        progress += getProcessingSpeed();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (level == null || failPreConditions() || recipe == null)
            return;
        if (!isExtracting())
            return;

        if ((AnimationTickHolder.getTicks() % Math.floor(256 / (getProcessingSpeed() / 2f))) == 0)
            level.playLocalSound(getNodePosition(), SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.5f, 0.1f, true);
    }

    public void spawnParticles() {
        if (recipe == null || level == null)
            return;
        if (!isExtracting())
            return;

        ItemStack item = recipe.getResultItem(level.registryAccess());
        if (item.isEmpty())
            item = new ItemStack(getNode().getBlock().asItem());

        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, item);
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, -getDrillOffset(1), 0.5f);
        offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
        float particlesSpeed = Math.clamp(getProcessingSpeed() / 2, 1, 25);
        Vec3 target = VecHelper.rotate(offset, getSpeed() < 0 ? -particlesSpeed : particlesSpeed, Direction.Axis.Y);

        Vec3 center = offset.add(Vec3.atBottomCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
    }

    public boolean isExtracting() {
        return animation.isWorking();
    }

    public int getProcessingSpeed() {
        return Math.round(Math.abs(getSpeed()));
    }

    public float getAbsTheoreticalSpeed() {
        return Math.abs(getTheoreticalSpeed());
    }

    public void setRecipe(ExtractingRecipe recipe) {
        // only reset progress on recipe change and not on load from nbt
        if (this.recipe != null && this.recipe != recipe)
            progress = 0;
        this.recipe = recipe;
        animation.start();
    }

    public void resetRecipe() {
        resetRecipe(false);
    }

    public void resetRecipe(boolean hard) {
        progress = 0;
        recipe = null;
        if (hard) {
            animation.reset();
        } else {
            animation.stop();
        }
    }

    public Optional<ExtractingRecipe> getRecipeFor(ExtractingRecipeInput input) {
        Optional<RecipeHolder<ExtractingRecipe>> holder = ModRecipeTypes.EXTRACTING.find(input, level);
        return holder.map(RecipeHolder::value);
    }

    public boolean failPreConditions() {
        return getNode().isEmpty()
               || !hasDrill()
               || isOutputFull()
               || !isSpeedRequirementFulfilled();
    }

    public BlockPos getNodePosition() {
        return getBlockPos().below(2);
    }

    public BlockState getNode() {
        return level == null ? Blocks.AIR.defaultBlockState() : level.getBlockState(getNodePosition());
    }

    public List<ItemStack> extractOutput() {
        List<ItemStack> output = new ArrayList<>(outputInv.getSlots());
        for (int i = 0; i < outputInv.getSlots(); i++) {
            output.add(outputInv.getStackInSlot(i));
            outputInv.setStackInSlot(i, ItemStack.EMPTY);
        }
        return output;
    }

    public boolean isOutputFull() {
        for (int i = 0; i < outputInv.getSlots(); i++) {
            ItemStack stack = outputInv.getStackInSlot(i);
            if (stack.getCount() >= stack.getMaxStackSize())
                return true;
        }
        return false;
    }

    public boolean hasDrill() {
        return !drillInv.getStackInSlot(0).isEmpty();
    }

    public ItemStack insertDrill(ItemStack drill) {
        resetRecipe(true);
        return this.drillInv.insertItem(0, drill.copy(), false);
    }

    public ItemStack extractDrill() {
        resetRecipe(true);
        return this.drillInv.extractItem(0, 1, false);
    }

    public ItemStack getDrill() {
        return drillInv.getStackInSlot(0);
    }

    public float getDrillOffset(float partialTicks) {
        if (level == null) return RETRACTED_DRILL_OFFSET;
        return animation.getValue(partialTicks);
    }

    public float getNodeDrillOffset() {
        if (level == null) return RETRACTED_DRILL_OFFSET;
        return getNode().getBlock() instanceof Extractable node ? node.getDrillOffset() : DEFAULT_DRILL_OFFSET;
    }

    public PartialModel getDrillModel() {
        return DrillPartialIndex.getOrDefaultModel(getDrill().getItem());
    }

    private void onDrillBreak(Item item) {
        resetRecipe(true);
        if (level != null)
            level.playSound(null, getBlockPos().below(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5f, 1);
    }

    public void setVirtualDrill(ItemStack stack) {
        if (!isVirtual()) return;
        drillInv.setStackInSlot(0, stack);
    }

    public void simulateExtraction(int ticks) {
        if (!isVirtual()) return;
        animation.simulate(ticks);
    }

    @Override
    public AABB createRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, -1, 0);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, drillInv);
        ItemHelper.dropContents(level, worldPosition, outputInv);
        if (level != null) {
            level.invalidateCapabilities(worldPosition);
            level.invalidateCapabilities(worldPosition.below());
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.put("DrillInventory", drillInv.serializeNBT(registries));
        compound.put("OutputInventory", outputInv.serializeNBT(registries));
        compound.putInt("Progress", progress);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        drillInv.deserializeNBT(registries, compound.getCompound("DrillInventory"));
        outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
        progress = compound.getInt("Progress");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean create = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        boolean recipeInfo = addRecipeInfoTooltip(tooltip);
        return create || recipeInfo;
    }

    protected boolean addRecipeInfoTooltip(List<Component> tooltip) {
        if (recipe == null)
            return false;

        int total = recipe.getProcessingDurationModified();
        int remaining = total - progress;
        long remainingMillis = (long) Math.max((remaining / getAbsTheoreticalSpeed()) * 50, 0);

        CreateRALang.text("Crafting Info:")
                    .forGoggles(tooltip);

        CreateRALang.text("Time: ")
                    .style(ChatFormatting.GRAY)
                    .add(RecipeModifiers.formatModifier(Config.server().recipeModifiers.duration))
                    .forGoggles(tooltip);

        CreateRALang.text(DurationFormatUtils.formatDuration(remainingMillis, "[H'h' ]m'm' ss's'"))
                    .style(ChatFormatting.DARK_AQUA)
                    .forGoggles(tooltip, 1);

        if (Config.client().advancedExtractorInfo.get()) {
            CreateRALang.number(Math.round(progress / getAbsTheoreticalSpeed()))
                        .style(ChatFormatting.DARK_AQUA)
                        .text(ChatFormatting.GRAY, " / ")
                        .add(CreateRALang.number(Math.round(total / getAbsTheoreticalSpeed())).style(ChatFormatting.DARK_AQUA))
                        .text(ChatFormatting.GRAY, " Ticks")
                        .forGoggles(tooltip, 1);
        }

        return true;
    }

    private class ExtractorInventoryHandler extends CombinedInvWrapper {
        public ExtractorInventoryHandler() {
            super(drillInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == outputInv)
                return false;
            return stack.is(ModTags.Items.DRILLS) && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == outputInv)
                return stack;
            if (!isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == drillInv)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    }
}
