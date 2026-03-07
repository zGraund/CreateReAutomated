package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.Config;
import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeEntity;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipeInput;
import com.github.zgraund.createreautomated.recipe.ModRecipes;
import com.github.zgraund.createreautomated.registry.ModTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ExtractorBlockEntity extends KineticBlockEntity {
    public static final float DEFAULT_DRILL_OFFSET = 0.8f;
    public static final float RETRACTED_DRILL_OFFSET = 0.55f;
    protected final ItemStackHandler drillInv = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }
    };
    protected final ItemStackHandler outputInv = new ItemStackHandler(1);
    private final IItemHandler capabilities = new ExtractorInventoryHandler();
    private int progress;
    private float animationProgress;
    private AnimationStatus animationStatus = AnimationStatus.IDLE;
    private ExtractorRecipe lastRecipe;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTRACTOR_BE.get(), pos, state);
    }

    public static void registerCapabilities(@Nonnull RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    if (state.getValue(ExtractorBlock.HALF) == DoubleBlockHalf.LOWER) {
                        pos = pos.above();
                        state = level.getBlockState(pos);
                        blockEntity = level.getBlockEntity(pos);
                        context = Direction.DOWN;
                    }
                    if (state.getValue(ExtractorBlock.HALF) == DoubleBlockHalf.UPPER && blockEntity instanceof ExtractorBlockEntity extractor)
                        return extractor.capabilities;
                    return null;
                },
                ModBlocks.EXTRACTOR.get()
        );
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null) return;

        tickAnimation();

        if (failPreConditions()) {
            resetDrill();
            return;
        }

        BlockPos nodePos = getNodePosition();
        ExtractorRecipeInput input = new ExtractorRecipeInput(drillInv.getStackInSlot(0), nodePos);
        if (lastRecipe == null || !lastRecipe.matches(input, level)) {
            Optional<ExtractorRecipe> recipe = getRecipe(input);
            if (recipe.isEmpty()) {
                resetDrill();
            } else {
                if (lastRecipe != null && lastRecipe != recipe.get())
                    progress = 0;
                lastRecipe = recipe.get();
            }
            return;
        }

        ItemStack result = lastRecipe.assemble(input, level.registryAccess());
        if (!canInsertResult(result)) {
            animationStatus = AnimationStatus.RETRACTING;
            return;
        }

        if (animationStatus != AnimationStatus.ENGAGED) {
            animationStatus = AnimationStatus.DEPLOYING;
            return;
        }

        if (level.isClientSide()) {
            spawnParticles();
            return;
        }

        progress += (int) getProcessingSpeed();

        if (progress >= lastRecipe.processingTime()) {
            BlockEntity blockEntity = level.getBlockEntity(nodePos);
            if (blockEntity instanceof OreNodeEntity nodeEntity) {
                nodeEntity.extract(1);
            }
            outputInv.insertItem(0, result, false);
            drillInv.getStackInSlot(0).hurtAndBreak(lastRecipe.durabilityLoss(), (ServerLevel) level, null, item -> {
                animationProgress = RETRACTED_DRILL_OFFSET;
                resetDrill();
                level.playSound(null, getBlockPos().below(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5f, 1);
            });
            progress = 0;
        }

        notifyUpdate();
    }

    public void tickAnimation() {
        switch (animationStatus) {
            case DEPLOYING -> {
                animationProgress += 0.01f;
                float max = getNodeMaxDrillOffset();
                if (animationProgress >= max) {
                    animationStatus = AnimationStatus.ENGAGED;
                    animationProgress = max;
                }
            }
            case RETRACTING -> {
                animationProgress -= 0.01f;
                if (animationProgress <= RETRACTED_DRILL_OFFSET) {
                    animationStatus = AnimationStatus.IDLE;
                    animationProgress = RETRACTED_DRILL_OFFSET;
                }
            }
        }
        sendData();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (level == null || failPreConditions() || lastRecipe == null)
            return;
        if (!shouldPlayAudioAndParticles())
            return;

        // TODO: test tick rate of audio
        if ((AnimationTickHolder.getTicks() % Math.floor(256 / (getProcessingSpeed() / 2))) == 0)
            level.playLocalSound(getNodePosition(), SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.5f, 0.1f, true);
    }

    public void spawnParticles() {
        if (lastRecipe == null || level == null)
            return;
        if (!shouldPlayAudioAndParticles())
            return;

        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, lastRecipe.getResultItem(level.registryAccess()));
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, -getDrillOffset(), 0.5f);
        offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
        float particlesSpeed = Math.clamp(getProcessingSpeed() / 2, 1, 25);
        Vec3 target = VecHelper.rotate(offset, getSpeed() < 0 ? -particlesSpeed : particlesSpeed, Direction.Axis.Y);

        Vec3 center = offset.add(Vec3.atBottomCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);

//        BlockPos node = getNodePosition();
//        level.addParticle(ParticleTypes.SMOKE, node.getX() + 0.4, node.getY() + 1, node.getZ() + 0.6, 0, 0.5, 0);
    }

    public boolean shouldPlayAudioAndParticles() {
        return animationStatus == AnimationStatus.ENGAGED;
    }

    public float getProcessingSpeed() {
        return Math.abs(getSpeed());
    }

    public void resetDrill() {
        progress = 0;
        lastRecipe = null;
        animationStatus = AnimationStatus.RETRACTING;
    }

    public Optional<ExtractorRecipe> getRecipe(ExtractorRecipeInput input) {
        if (level == null) throw new IllegalStateException("ExtractorBlockEntity with null level");
        return level.getRecipeManager()
                    .getRecipeFor(ModRecipes.EXTRACTOR_RECIPE.get(), input, level)
                    .map(RecipeHolder::value);
    }

    public boolean canInsertResult(ItemStack stack) {
        return outputInv.insertItem(0, stack, true).isEmpty();
    }

    public boolean failPreConditions() {
        if (level == null) return true;
        return level.getBlockState(getNodePosition()).isEmpty()
               || !hasDrill()
               || isOutputFull()
               || getProcessingSpeed() < ExtractorBlock.MIN_SPEED.getSpeedValue();
    }

    public BlockPos getNodePosition() {
        return getBlockPos().below(2);
    }

    public float getNodeMaxDrillOffset() {
        if (level == null) return RETRACTED_DRILL_OFFSET;
        BlockState state = level.getBlockState(getNodePosition());
        return state.getBlock() instanceof OreNodeBlock node ? node.getDrillOffset() : DEFAULT_DRILL_OFFSET;
    }

    public boolean hasDrill() {
        return !drillInv.getStackInSlot(0).isEmpty();
    }

    public boolean isOutputFull() {
        ItemStack output = outputInv.getStackInSlot(0);
        return output.getCount() >= output.getMaxStackSize();
    }

    public float getDrillOffset() {
        if (level == null) return RETRACTED_DRILL_OFFSET;
        return animationProgress;
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, drillInv);
        ItemHelper.dropContents(level, worldPosition, outputInv);
    }

    @Override
    protected void write(@Nonnull CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.put("DrillInventory", drillInv.serializeNBT(registries));
        compound.put("OutputInventory", outputInv.serializeNBT(registries));
        compound.putInt("Progress", progress);
        compound.putFloat("Animation", animationProgress);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(@Nonnull CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        drillInv.deserializeNBT(registries, compound.getCompound("DrillInventory"));
        outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
        progress = compound.getInt("Progress");
        animationProgress = compound.getFloat("Animation");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (Config.Client.DEBUG_EXTRACTOR_INFO.getAsBoolean()) {
            MutableComponent component = Component.empty();
            component.append(Component.literal("    Crafting progress: ").withStyle(ChatFormatting.GRAY));
            int percentage = lastRecipe != null ? (progress * 100) / lastRecipe.processingTime() : 0;
            component.append(Component.literal(percentage + "%").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(component);
            List<String> s = lastRecipe.nodeSet()
                                       .stream()
                                       .map(Holder::getRegisteredName)
                                       .toList();
            Component s1 = ComponentUtils.formatList(s);
            Component recipe = Component.literal(Arrays.toString(lastRecipe.drill().getItems()));
            tooltip.add(s1);
            tooltip.add(recipe);
        }
        return true;
    }

    public void setVirtualDrill(ItemStack stack) {
        if (!isVirtual()) return;
        drillInv.setStackInSlot(0, stack);
    }

    public enum AnimationStatus {
        IDLE, DEPLOYING, ENGAGED, RETRACTING
    }

    private class ExtractorInventoryHandler extends CombinedInvWrapper {
        public ExtractorInventoryHandler() {
            super(drillInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == outputInv)
                return false;
            return stack.is(ModTags.Items.DRILL_ANY) && super.isItemValid(slot, stack);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == outputInv)
                return stack;
            if (!isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == drillInv)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    }
}
