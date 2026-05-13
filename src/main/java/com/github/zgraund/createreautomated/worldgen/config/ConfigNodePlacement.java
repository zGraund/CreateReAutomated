package com.github.zgraund.createreautomated.worldgen.config;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.config.Worldgen;
import com.github.zgraund.createreautomated.registry.ModPlacementModifiers;
import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfigNodePlacement extends PlacementModifier {
    public static final MapCodec<ConfigNodePlacement> CODEC =
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension").xmap(ConfigNodePlacement::new, placement -> placement.config.dimension);

    private final Worldgen.DimensionConfig config;

    public ConfigNodePlacement(ResourceKey<Level> dimension) {
        this.config = Config.common().worldgen.getConfig(dimension);
    }

    @Contract(" -> new")
    public static ConfigNodePlacement overworld() {
        return new ConfigNodePlacement(Level.OVERWORLD);
    }

    @Contract(" -> new")
    public static ConfigNodePlacement nether() {
        return new ConfigNodePlacement(Level.NETHER);
    }

    public int sample(RandomSource random) {
        int min = config.minY.get();
        int max = config.maxY.get();
        if (min > max) {
            CreateReAutomated.LOGGER.warn("Empty height range in config for dimension: {}", config.dimension);
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
