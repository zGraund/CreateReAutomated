package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.registry.ModDataComponents;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OreNodeEntity extends SyncedBlockEntity {
    private int remaining;

    public OreNodeEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ORE_NODE_BE.get(), pos, blockState);
        remaining = ((OreNodeBlock) blockState.getBlock()).getMaxExtractions();
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

        if (this.remaining <= 0) {
            level.setBlockAndUpdate(pos, node.baseRock);
            level.playSound(null, pos, node.getSoundType(currentState, level, pos, null).getBreakSound(), SoundSource.BLOCKS);
            return;
        }

        int newState = node.getStateFromQuantity(remaining);

        if (newState != currentState.getValue(OreNodeBlock.DEPLETION)) {
            level.setBlockAndUpdate(pos, currentState.setValue(OreNodeBlock.DEPLETION, newState));
            level.playSound(null, pos, node.getSoundType(currentState, level, pos, null).getBreakSound(), SoundSource.BLOCKS);
        }
    }

    public boolean canExtract(int quantity) {
        return remaining >= quantity;
    }

    public int getRemaining() {
        return remaining;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        remaining = componentInput.getOrDefault(ModDataComponents.NODE_REMAINING_EXTRACTIONS.get(), remaining);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.NODE_REMAINING_EXTRACTIONS.get(), remaining);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("remaining");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        remaining = tag.getInt("remaining");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("remaining", remaining);
    }
}
