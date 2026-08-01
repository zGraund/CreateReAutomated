package com.github.zgraund.createreautomated.worldgen.feature;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.neoforged.neoforge.common.util.TriState;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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
        TriState found = TriState.DEFAULT;

        for (OreConfiguration.TargetBlockState targetState : encasedNodeConfiguration.targetStates) {
            if (targetState.target.test(worldgenlevel.getBlockState(blockpos), context.random())) {

                if (!encasedNodeConfiguration.allowSameVein()) {
                    if (found.isDefault())
                        found = findNode(context);
                    if (found.isTrue())
                        return true;
                }

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

    public TriState findNode(@Nonnull FeaturePlaceContext<EncasedNodeConfiguration> context) {
        int maxDepth = 8;

        EncasedNodeConfiguration encasedNodeConfiguration = context.config();
        WorldGenLevel level = context.level();
        BlockPos originPos = context.origin();

        LongSet visited = new LongOpenHashSet(List.of(originPos.asLong()));
        Deque<Pair<BlockPos, Integer>> deque = new ArrayDeque<>();
        deque.add(Pair.of(originPos, 0));

        while (!deque.isEmpty()) {
            Pair<BlockPos, Integer> pair = deque.pop();
            BlockPos currentPos = pair.getFirst();
            int depth = pair.getSecond();
            for (Direction direction : Direction.values()) {
                BlockPos nextPos = currentPos.relative(direction);

                for (OreConfiguration.TargetBlockState targetState : encasedNodeConfiguration.targetStates) {
                    if (level.getBlockState(nextPos).is(targetState.state.getBlock())) {
                        return TriState.TRUE;
                    }

                    if (targetState.target.test(level.getBlockState(nextPos), level.getRandom()) && visited.add(nextPos.asLong()) && depth <= maxDepth) {
                        deque.add(Pair.of(nextPos, depth + 1));
                        break;
                    }
                }
            }
        }

        return TriState.FALSE;
    }
}
