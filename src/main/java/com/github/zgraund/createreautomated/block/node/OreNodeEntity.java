package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class OreNodeEntity extends BlockEntity {
    private int remaining;

    public OreNodeEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ORE_NODE_BE.get(), pos, blockState);
        remaining = ((OreNodeBlock) blockState.getBlock()).MAX_EXTRACTIONS;
    }

    public void extract(int quantity) {
        remaining -= quantity;
    }

    @Override
    protected void applyImplicitComponents(@Nonnull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
//        remaining = componentInput.getOrDefault(DataComponents.)
    }

    @Override
    protected void collectImplicitComponents(@Nonnull DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
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
        tag.putInt("remaining", remaining);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        remaining = tag.getInt("remaining");
    }

    public int getRemaining() {
        return remaining;
    }
}
