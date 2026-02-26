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
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ExtractorBlockEntity extends KineticBlockEntity {
    protected final ItemStackHandler drillInv = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }
    };
    protected final ItemStackHandler outputInv = new ItemStackHandler(1);
    protected int progress;
    private ExtractorRecipe lastRecipe;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTRACTOR_BE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null) return;
        if (!fulfillPreConditions()) return;

        BlockState blockState = getNodeBelow();
        if (!(blockState.getBlock() instanceof OreNodeBlock)) return;

        ExtractorRecipeInput input = new ExtractorRecipeInput(drillInv.getStackInSlot(0), blockState);
        if (lastRecipe == null || !lastRecipe.matches(input, level)) {
            Optional<ExtractorRecipe> recipe = getRecipe(input);
            if (recipe.isEmpty()) {
                lastRecipe = null;
            } else {
                lastRecipe = recipe.get();
                progress = lastRecipe.processingTime();
            }
            sendData();
            return;
        }

        progress -= getProcessingSpeed();
        if (level.isClientSide()) {
            //TODO: spawn particles
            return;
        }

        if (progress <= 0) {
            BlockEntity blockEntity = level.getBlockEntity(getBlockPos().below(2));
            if (blockEntity instanceof OreNodeEntity nodeEntity) {
                // TODO: consume node below
                nodeEntity.extract(1);
            }
            ItemStack result = lastRecipe.assemble(input, level.registryAccess());
            outputInv.insertItem(0, result, false);
            progress = lastRecipe.processingTime();
            setChanged();
        }

        sendData();
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

    public boolean fulfillPreConditions() {
        return hasDrill() && !isOutputFull() && isSpeedRequirementFulfilled();
    }

    public BlockState getNodeBelow() {
        if (level == null) throw new IllegalStateException("ExtractorBlockEntity#getNodeBelow called with null level");
        return level.getBlockState(getBlockPos().below(2));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        ItemStack out = outputInv.getStackInSlot(0);
        // TODO: maybe make a canProcess() method?
        if (!isSpeedRequirementFulfilled() || !hasDrill() || out.getCount() >= out.getMaxStackSize())
            return;

        SoundScapes.play(SoundScapes.AmbienceGroup.CRUSHING, worldPosition, 0.1f);
    }

    public boolean hasDrill() {
        return !drillInv.getStackInSlot(0).isEmpty();
    }

    public boolean isOutputFull() {
        ItemStack output = outputInv.getStackInSlot(0);
        return output.getCount() >= output.getMaxStackSize();
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
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(@Nonnull CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        drillInv.deserializeNBT(registries, compound.getCompound("DrillInventory"));
        outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
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
