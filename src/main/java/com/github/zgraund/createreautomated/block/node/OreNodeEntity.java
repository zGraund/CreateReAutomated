package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.registry.ModDataComponents;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class OreNodeEntity extends SyncedBlockEntity {
    private int remaining;

    public OreNodeEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ORE_NODE_BE.get(), pos, blockState);
        remaining = ((OreNodeBlock) blockState.getBlock()).MAX_EXTRACTIONS;
    }

    public void extract(int quantity) {
        if (level == null || level.isClientSide() || !canExtract(quantity)) return;

        remaining -= quantity;

        updateBlockState();
        notifyUpdate();
    }

    public void updateBlockState() {
        if (level == null) return;

        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof OreNodeBlock node)) return;

        BlockPos pos = getBlockPos();
        OreNodeBlock.Resources newState = node.getStateFromQuantity(remaining);

        if (newState != currentState.getValue(OreNodeBlock.RESOURCES)) {
            level.setBlockAndUpdate(pos, currentState.setValue(OreNodeBlock.RESOURCES, newState));
            // TODO: funny but we should probably change this
            level.playSound(null, pos, SoundEvents.GOAT_SCREAMING_AMBIENT, SoundSource.BLOCKS);
        }
    }

    public boolean canExtract(int quantity) {
        return remaining >= quantity;
    }

    public int getRemaining() {
        return remaining;
    }

    @Override
    protected void applyImplicitComponents(@Nonnull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        remaining = componentInput.getOrDefault(ModDataComponents.NODE_QUANTITY.get(), remaining);
    }

    @Override
    protected void collectImplicitComponents(@Nonnull DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.NODE_QUANTITY.get(), remaining);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(@Nonnull CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("remaining");
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        remaining = tag.getInt("remaining");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("remaining", remaining);
    }
}
