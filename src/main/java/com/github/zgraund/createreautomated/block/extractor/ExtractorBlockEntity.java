package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.api.block.Extractable;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipeInput;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.github.zgraund.createreautomated.registry.ModTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    protected final ItemStackHandler outputInv = new ItemStackHandler(6);
    private final IItemHandler capabilities = new ExtractorInventoryHandler();
    private int progress;
    private float animationProgress = RETRACTED_DRILL_OFFSET;
    private AnimationStatus animationStatus = AnimationStatus.IDLE;
    @Nullable
    private ExtractorRecipe lastRecipe;

    public ExtractorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(@Nonnull RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    if (state.getValue(ExtractorBlock.HALF) == DoubleBlockHalf.LOWER) {
                        pos = pos.above();
                        state = level.getBlockState(pos);
                        blockEntity = level.getBlockEntity(pos);
                        // in case I need to change the capability based on direction
                        // context = Direction.DOWN;
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

        // TODO: the extractor animation/progress should only tick on server
        //       partly because in 1.21.2 recipes are no longer synced and and
        //       so that the animation is not broken by the server running at a
        //       different tick rate than the client.
        //       so this method need to be refactored

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

        if (animationStatus != AnimationStatus.ENGAGED) {
            animationStatus = AnimationStatus.DEPLOYING;
            return;
        }

        if (level.isClientSide()) {
            spawnParticles();
            return;
        }

        progress += (int) getProcessingSpeed();

        if (progress >= lastRecipe.getProcessingDuration()) {
            if (level.getBlockState(nodePos).getBlock() instanceof Extractable node) {
                node.extract(lastRecipe.extractionQuantity(), nodePos, level);
            }
            lastRecipe.rollResults(level.random).forEach(result ->
                    ItemHandlerHelper.insertItemStacked(outputInv, result, false)
            );
            drillInv.getStackInSlot(0).hurtAndBreak(lastRecipe.durabilityCost(), (ServerLevel) level, null, item -> {
                resetDrill(true);
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
    }

    public boolean shouldPlayAudioAndParticles() {
        return animationStatus == AnimationStatus.ENGAGED;
    }

    public float getProcessingSpeed() {
        return Math.abs(getSpeed());
    }

    public void resetDrill() {
        resetDrill(false);
    }

    public void resetDrill(boolean hard) {
        progress = 0;
        lastRecipe = null;
        if (hard) {
            animationStatus = AnimationStatus.IDLE;
            animationProgress = RETRACTED_DRILL_OFFSET;
        } else {
            animationStatus = AnimationStatus.RETRACTING;
        }
    }

    public Optional<ExtractorRecipe> getRecipe(ExtractorRecipeInput input) {
        if (level == null) return Optional.empty();
        Optional<RecipeHolder<ExtractorRecipe>> holder = ModRecipeTypes.EXTRACTING.find(input, level);
        return holder.map(RecipeHolder::value);
    }

    public boolean failPreConditions() {
        return level == null
               || level.getBlockState(getNodePosition()).isEmpty()
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

    public float getNodeMaxDrillOffset() {
        if (level == null) return RETRACTED_DRILL_OFFSET;
        return getNode().getBlock() instanceof Extractable node ? node.getDrillOffset() : DEFAULT_DRILL_OFFSET;
    }

    public boolean hasDrill() {
        return !drillInv.getStackInSlot(0).isEmpty();
    }

    public Item getDrill() {
        return drillInv.getStackInSlot(0).getItem();
    }

    public boolean isOutputFull() {
        for (int i = 0; i < outputInv.getSlots(); i++) {
            ItemStack stack = outputInv.getStackInSlot(i);
            if (stack.getCount() >= stack.getMaxStackSize())
                return true;
        }
        return false;
    }

    public float getDrillOffset() {
        if (level == null) return RETRACTED_DRILL_OFFSET;
        return animationProgress;
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
        boolean create = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        boolean reAutomated = false;
        if (Config.client().debugExtractorOverlay.get() && lastRecipe != null) {
            reAutomated = true;
            // For this use case the Create lang builder is good enough
            CreateLang.text("Crafting progress: ")
                      .style(ChatFormatting.GRAY)
                      .add(CreateLang.text((progress * 100) / lastRecipe.getProcessingDuration() + "%").style(ChatFormatting.DARK_GRAY))
                      .forGoggles(tooltip);
            CreateLang.itemName(drillInv.getStackInSlot(0))
                      .style(ChatFormatting.DARK_GRAY)
                      .forGoggles(tooltip, 1);
            CreateLang.blockName(getNode())
                      .style(ChatFormatting.DARK_GRAY)
                      .forGoggles(tooltip, 1);
            lastRecipe.getRollableResults().forEach(output ->
                    CreateLang.text(" -> ")
                              .style(ChatFormatting.DARK_GRAY)
                              .add(CreateLang.text(output.getStack().getCount() + "x ").style(ChatFormatting.DARK_GRAY))
                              .add(CreateLang.itemName(output.getStack()).style(ChatFormatting.DARK_GRAY))
                              .space()
                              .add(CreateLang.text(output.getChance() * 100 + "%").style(ChatFormatting.DARK_GRAY))
                              .forGoggles(tooltip, 1)
            );
        }
        return create || reAutomated;
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
            return stack.is(ModTags.Items.DRILLS) && super.isItemValid(slot, stack);
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
