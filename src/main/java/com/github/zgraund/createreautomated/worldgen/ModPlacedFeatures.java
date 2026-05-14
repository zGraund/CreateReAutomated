package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.worldgen.config.ConfigNodePlacement;
import com.github.zgraund.createreautomated.worldgen.config.ConfigPlacementFilter;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.data.worldgen.placement.PlacementUtils.register;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> OVERWORLD_ORE_NODE_PLACED_KEY = registerKey("ore_node_placed");
    public static final ResourceKey<PlacedFeature> NETHER_ORE_NODE_PLACED_KEY = registerKey("nether_ore_node_placed");

    public static void bootstrap(@Nonnull BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(
                context,
                OVERWORLD_ORE_NODE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_ORE_NODE_KEY),
                List.of(
                        ConfigPlacementFilter.INSTANCE,
                        ConfigNodePlacement.overworld(),
                        InSquarePlacement.spread()
                )
        );

        register(
                context,
                NETHER_ORE_NODE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.NETHER_ORE_NODE_KEY),
                List.of(
                        ConfigPlacementFilter.INSTANCE,
                        ConfigNodePlacement.nether(),
                        InSquarePlacement.spread()
                )
        );
    }

    @Nonnull
    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, CreateReAutomated.asResource(name));
    }
}
