package com.github.zgraund.createreautomated.worldgen.config;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.config.Worldgen;
import com.github.zgraund.createreautomated.registry.ModPlacementModifiers;
import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfigNodePlacement extends PlacementModifier {
    public static final MapCodec<ConfigNodePlacement> CODEC =
            Worldgen.NodeGroup.CODEC.fieldOf("group").xmap(ConfigNodePlacement::of, placement -> placement.config.group);

    private final Worldgen.NodeConfig config;

    private ConfigNodePlacement(Worldgen.NodeGroup group) {
        this.config = group.getConfig();
    }

    public static ConfigNodePlacement of(Worldgen.NodeGroup group) {
        return new ConfigNodePlacement(group);
    }

    public int sample(RandomSource random) {
        int min = config.minY.get();
        int max = config.maxY.get();
        if (min > max) {
            CreateReAutomated.LOGGER.warn("Empty height range in config for group: {}", config.group.name());
            return min;
        } else {
            return Mth.randomBetweenInclusive(random, min, max);
        }
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        return IntStream.range(0, config.tries.get()).mapToObj(n -> pos.atY(sample(random)));
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.CONFIG_NODE_PLACEMENT.get();
    }
}
