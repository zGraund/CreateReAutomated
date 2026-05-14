package com.github.zgraund.createreautomated.worldgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import javax.annotation.Nonnull;

public class EncasedNodeFeature extends Feature<EncasedNodeConfiguration> {
    public EncasedNodeFeature() {
        super(EncasedNodeConfiguration.CODEC);
    }

    @Override
    public boolean place(@Nonnull FeaturePlaceContext<EncasedNodeConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        BlockPos blockpos = context.origin();
        EncasedNodeConfiguration encasedNodeConfiguration = context.config();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (OreConfiguration.TargetBlockState targetState : encasedNodeConfiguration.targetStates) {
            if (targetState.target.test(worldgenlevel.getBlockState(blockpos), context.random())) {
                int requiredFaces = encasedNodeConfiguration.getFaces(context.random());
                if (requiredFaces > 0) {
                    for (Direction direction : Direction.values()) {
                        if (targetState.target.test(worldgenlevel.getBlockState(mutablePos.setWithOffset(blockpos, direction)), context.random()))
                            requiredFaces--;
                        if (requiredFaces <= 0) {
                            break;
                        }
                    }
                }
                if (requiredFaces <= 0) {
                    worldgenlevel.setBlock(blockpos, targetState.state, 2);
                    return true;
                }
            }
        }

        return true;
    }
}
