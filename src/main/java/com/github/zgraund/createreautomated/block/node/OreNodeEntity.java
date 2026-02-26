package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class OreNodeEntity extends BlockEntity {
    private int remaining;
    public ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) return remaining;
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) remaining = value;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public OreNodeEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ORE_NODE_BE.get(), pos, blockState);
        remaining = ((OreNodeBlock) blockState.getBlock()).MAX_EXTRACTIONS;
    }

    public boolean tryExtract(int quantity) {
        if (level != null && level.isClientSide()) return false;
        BlockState blockState = getBlockState();
        if (blockState.getBlock() instanceof OreNodeBlock node) {
            if (node.isInfinite()) return true;
            if (remaining < quantity) return false;
            remaining -= quantity;
            BlockPos pos = getBlockPos();
            OreNodeBlock.Resources newState = OreNodeBlock.Resources.fromQuantity(remaining, node.MAX_EXTRACTIONS);
            if (newState != blockState.getValue(OreNodeBlock.RESOURCES)) {
                level.setBlockAndUpdate(pos, blockState.setValue(OreNodeBlock.RESOURCES, newState));
                // TODO: funny but we should probably change this
                level.playSound(null, pos, SoundEvents.GOAT_SCREAMING_AMBIENT, SoundSource.BLOCKS);
            }
            setChanged();
        }
        return true;
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
