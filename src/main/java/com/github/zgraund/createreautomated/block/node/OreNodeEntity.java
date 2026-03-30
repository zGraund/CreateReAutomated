package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.registry.ModDataComponents;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OreNodeEntity extends SyncedBlockEntity {
    private int yield;

    public OreNodeEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        yield = ((OreNodeBlock) state.getBlock()).getMaxExtractions();
    }

    public void extract(int quantity) {
        if (level == null || level.isClientSide() || !canExtract(quantity)) return;

        yield -= quantity;

        updateBlockState();
        notifyUpdate();
    }

    public void updateBlockState() {
        if (level == null) return;

        BlockState currentState = getBlockState();
        if (!(currentState.getBlock() instanceof OreNodeBlock node)) return;

        BlockPos pos = getBlockPos();

        if (this.yield <= 0) {
            level.setBlockAndUpdate(pos, node.baseRock);
            level.playSound(null, pos, node.getSoundType(currentState, level, pos, null).getBreakSound(), SoundSource.BLOCKS);
            return;
        }

        int newState = node.getStateFromQuantity(yield);

        if (newState != currentState.getValue(OreNodeBlock.DEPLETION)) {
            level.setBlockAndUpdate(pos, currentState.setValue(OreNodeBlock.DEPLETION, newState));
            level.playSound(null, pos, node.getSoundType(currentState, level, pos, null).getBreakSound(), SoundSource.BLOCKS);
        }
    }

    public boolean canExtract(int quantity) {
        return yield >= quantity;
    }

    public int getYield() {
        return yield;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        yield = componentInput.getOrDefault(ModDataComponents.NODE_REMAINING_EXTRACTIONS.get(), yield);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.NODE_REMAINING_EXTRACTIONS.get(), yield);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("Yield");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        yield = tag.getInt("Yield");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Yield", yield);
    }
}
