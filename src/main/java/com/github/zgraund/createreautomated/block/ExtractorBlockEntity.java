package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.item.ModItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class ExtractorBlockEntity extends KineticBlockEntity {
    protected final ItemStackHandler drillInv = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }
    };
    protected final ItemStackHandler outputInv = new ItemStackHandler(1);
    protected int progress;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTRACTOR_BE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) return;
        if (hasDrill()) {
            progress++;
        }
        if (progress >= 1 && level != null) {
            Item below = level.getBlockState(worldPosition.below().below()).getBlock().asItem();
            outputInv.insertItem(0, new ItemStack(below), false);
            progress = 0;
        }
        sendData();
        setChanged();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        ItemStack out = outputInv.getStackInSlot(0);
        // TODO: maybe make a canProcess() method?
        if (Math.abs(getSpeed()) <= ExtractorBlock.MIN_SPEED.getSpeedValue() || !hasDrill() || out.getCount() >= out.getMaxStackSize())
            return;

        SoundScapes.play(SoundScapes.AmbienceGroup.CRUSHING, worldPosition, 0.1f);
    }

    public boolean hasDrill() {
        return !drillInv.getStackInSlot(0).isEmpty();
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
