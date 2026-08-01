package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.config.Worldgen;
import com.github.zgraund.createreautomated.worldgen.config.ConfigNodePlacement;
import com.github.zgraund.createreautomated.worldgen.config.ConfigPlacementFilter;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.data.worldgen.placement.PlacementUtils.register;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> COPPER_NODE_PLACED_KEY = registerKey("copper_node_placed");
    public static final ResourceKey<PlacedFeature> ZINC_NODE_PLACED_KEY = registerKey("zinc_node_placed");
    public static final ResourceKey<PlacedFeature> IRON_NODE_PLACED_KEY = registerKey("iron_node_placed");
    public static final ResourceKey<PlacedFeature> GOLD_NODE_PLACED_KEY = registerKey("gold_node_placed");
    public static final ResourceKey<PlacedFeature> DIAMOND_NODE_PLACED_KEY = registerKey("diamond_node_placed");

    public static final ResourceKey<PlacedFeature> NETHER_GOLD_NODE_PLACED_KEY = registerKey("nether_gold_node_placed");
    public static final ResourceKey<PlacedFeature> NETHER_QUARTZ_NODE_PLACED_KEY = registerKey("nether_quartz_node_placed");
    public static final ResourceKey<PlacedFeature> ANCIENT_DEBRIS_NODE_PLACED_KEY = registerKey("ancient_debris_node_placed");

    public static void bootstrap(@Nonnull BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> cf = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, COPPER_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.COPPER_NODE_KEY), placement(Worldgen.NodeGroup.COPPER));
        register(context, ZINC_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.ZINC_NODE_KEY), placement(Worldgen.NodeGroup.ZINC));
        register(context, IRON_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.IRON_NODE_KEY), placement(Worldgen.NodeGroup.IRON));
        register(context, GOLD_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.GOLD_NODE_KEY), placement(Worldgen.NodeGroup.GOLD));
        register(context, DIAMOND_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.DIAMOND_NODE_KEY), placement(Worldgen.NodeGroup.DIAMOND));

        register(context, NETHER_GOLD_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.NETHER_GOLD_NODE_KEY), placement(Worldgen.NodeGroup.NETHER_GOLD));
        register(context, NETHER_QUARTZ_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.NETHER_QUARTZ_NODE_KEY), placement(Worldgen.NodeGroup.NETHER_QUARTZ));
        register(context, ANCIENT_DEBRIS_NODE_PLACED_KEY, cf.getOrThrow(ModConfiguredFeatures.ANCIENT_DEBRIS_NODE_KEY),
                placement(Worldgen.NodeGroup.ANCIENT_DEBRIS));
    }

    @Nonnull
    @Contract("_ -> new")
    private static @Unmodifiable List<PlacementModifier> placement(Worldgen.NodeGroup group) {
        return List.of(ConfigPlacementFilter.INSTANCE, ConfigNodePlacement.of(group), InSquarePlacement.spread());
    }

    @Nonnull
    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, CreateReAutomated.asResource(name));
    }
}
