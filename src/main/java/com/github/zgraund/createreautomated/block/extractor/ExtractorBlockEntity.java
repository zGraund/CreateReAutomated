package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeEntity;
import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipeInput;
import com.github.zgraund.createreautomated.recipe.ModRecipes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ExtractorBlockEntity extends KineticBlockEntity {
    public static final float DEFAULT_DRILL_OFFSET = 0.8f;
    public static final float RETRACTED_DRILL_OFFSET = 0.6f;
    protected final ItemStackHandler drillInv = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }
    };
    protected final ItemStackHandler outputInv = new ItemStackHandler(1);
    private int progress;
    private boolean isCrafting;
    private ExtractorRecipe lastRecipe;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTRACTOR_BE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null) return;
        // FIXME: this is very ugly and need refactor
        if (failPreConditions()) {
            isCrafting = false;
            setChanged();
            sendData();
            return;
        }

        BlockPos nodePos = getNodePosition();
        ExtractorRecipeInput input = new ExtractorRecipeInput(drillInv.getStackInSlot(0), nodePos);
        if (lastRecipe == null || !lastRecipe.matches(input, level)) {
            Optional<ExtractorRecipe> recipe = getRecipe(input);
            if (recipe.isEmpty()) {
                lastRecipe = null;
                isCrafting = false;
            } else {
                lastRecipe = recipe.get();
                progress = lastRecipe.processingTime();
            }
            setChanged();
            sendData();
            return;
        }

        progress -= getProcessingSpeed();
        isCrafting = true;
        if (level.isClientSide()) {
            spawnParticles();
            return;
        }

        if (progress <= 0) {
            BlockEntity blockEntity = level.getBlockEntity(nodePos);
            if (blockEntity instanceof OreNodeEntity nodeEntity) {
                ItemStack result = lastRecipe.assemble(input, level.registryAccess());
                if (canInsertResult(result)) {
                    nodeEntity.extract(1);
                    outputInv.insertItem(0, result, false);
                    progress = lastRecipe.processingTime();
                } else {
                    progress = 0;
                    isCrafting = false;
                }
            }
        }

        setChanged();
        sendData();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (!isCrafting || failPreConditions())
            return;

        SoundScapes.play(SoundScapes.AmbienceGroup.CRUSHING, worldPosition, 0.1f);
    }

    public void spawnParticles() {
        if (lastRecipe == null || level == null)
            return;
        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, lastRecipe.getResultItem(level.registryAccess()));
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, -getDrillOffset(), 0.5f);
        offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y);

        Vec3 center = offset.add(Vec3.atBottomCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
    }

    public int getProcessingSpeed() {
        return (int) Math.abs(getSpeed());
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
        return level.getBlockState(getNodePosition()).isEmpty() && !hasDrill() && isOutputFull() && Math.abs(getSpeed()) < ExtractorBlock.MIN_SPEED.getSpeedValue();
    }

    public BlockPos getNodePosition() {
        return getBlockPos().below(2);
    }

    public boolean hasDrill() {
        return !drillInv.getStackInSlot(0).isEmpty();
    }

    public boolean isOutputFull() {
        ItemStack output = outputInv.getStackInSlot(0);
        return output.getCount() >= output.getMaxStackSize();
    }

    public float getDrillOffset() {
        if (level == null) return DEFAULT_DRILL_OFFSET;
        if (!isCrafting)
            return RETRACTED_DRILL_OFFSET;
        BlockState state = level.getBlockState(getNodePosition());
        if (state.getBlock() instanceof OreNodeBlock node)
            return node.getDrillOffset(state);
        return DEFAULT_DRILL_OFFSET;
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
        compound.putBoolean("IsCrafting", isCrafting);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(@Nonnull CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        drillInv.deserializeNBT(registries, compound.getCompound("DrillInventory"));
        outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
        isCrafting = compound.getBoolean("IsCrafting");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    private class ExtractorInventoryHandler extends CombinedInvWrapper {
        public ExtractorInventoryHandler() {
            super(drillInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return false;
            return stack.is(ModItems.DRILLHEAD) && super.isItemValid(slot, stack);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return stack;
            if (!isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (drillInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    }
}
