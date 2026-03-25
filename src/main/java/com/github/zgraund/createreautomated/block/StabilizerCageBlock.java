package com.github.zgraund.createreautomated.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class StabilizerCageBlock extends TransparentBlock {
    public StabilizerCageBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
    }
}
