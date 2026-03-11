package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.data.worldgen.placement.PlacementUtils.register;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> OVERWORLD_ORE_NODE_PLACED_KEY = registerKey("ore_node_placed");

    public static void bootstrap(@Nonnull BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(
                context,
                OVERWORLD_ORE_NODE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_ORE_NODE_KEY),
                defaultNodePlacement()
        );
    }

    @Nonnull
    @Contract(" -> new")
    public static @Unmodifiable List<PlacementModifier> defaultNodePlacement() {
        return nodePlacement(50, -64, 128);
    }

    @Nonnull
    @Contract("_, _, _ -> new")
    public static @Unmodifiable List<PlacementModifier> nodePlacement(int count, int min, int max) {
        return List.of(
                ConfigPlacementFilter.INSTANCE,
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max))
        );
    }

    @Nonnull
    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, CreateReAutomated.asResource(name));
    }
}
