package com.github.zgraund.createreautomated.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import javax.annotation.Nonnull;

public class OreNodeFeature extends Feature<ReplaceBlockConfiguration> {
    public OreNodeFeature() {
        super(ReplaceBlockConfiguration.CODEC);
    }

    @Override
    public boolean place(@Nonnull FeaturePlaceContext<ReplaceBlockConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        BlockPos blockpos = context.origin();
        ReplaceBlockConfiguration replaceblockconfiguration = context.config();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (OreConfiguration.TargetBlockState targetState : replaceblockconfiguration.targetStates) {
            if (targetState.target.test(worldgenlevel.getBlockState(blockpos), context.random())) {
                int adjacentNodes = 0;
                for (Direction direction : Direction.values()) {
                    if (targetState.target.test(worldgenlevel.getBlockState(mutablePos.setWithOffset(blockpos, direction)), context.random()))
                        adjacentNodes++;
                    if (adjacentNodes >= 3) {
                        worldgenlevel.setBlock(blockpos, targetState.state, 2);
                        return true;
                    }
                }
            }
        }

        return true;
    }
}
