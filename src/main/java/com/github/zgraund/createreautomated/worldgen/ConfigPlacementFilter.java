package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.registry.ModPlacementModifiers;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigPlacementFilter extends PlacementFilter {
    public static final ConfigPlacementFilter INSTANCE = new ConfigPlacementFilter();
    public static final MapCodec<ConfigPlacementFilter> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        return Config.common().worldgen.get();
    }

    @Nonnull
    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.CONFIG_FILTER.get();
    }
}
