package com.github.zgraund.createreautomated.block.node;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OreNodeEntity extends BlockEntity {
    public OreNodeEntity(BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
}
