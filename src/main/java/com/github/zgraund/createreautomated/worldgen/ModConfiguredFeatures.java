package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.data.worldgen.features.FeatureUtils.register;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_NODE_KEY = registerKey("ore_node");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_ORE_NODE_KEY = registerKey("nether_ore_node");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        List<OreConfiguration.TargetBlockState> overworld = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.COPPER_ORE), ModBlocks.COPPER_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.IRON_ORE), ModBlocks.IRON_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.GOLD_ORE), ModBlocks.GOLD_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DIAMOND_ORE), ModBlocks.DIAMOND_NODE.get().unstable()),

                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_COPPER_ORE), ModBlocks.DEEPSLATE_COPPER_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_IRON_ORE), ModBlocks.DEEPSLATE_IRON_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_GOLD_ORE), ModBlocks.DEEPSLATE_GOLD_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_DIAMOND_ORE), ModBlocks.DEEPSLATE_DIAMOND_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> nether = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.NETHER_GOLD_ORE), ModBlocks.NETHER_GOLD_NODE.get().unstable())
        );

        register(context, OVERWORLD_ORE_NODE_KEY, ModFeatures.ORE_NODE_FEATURE.get(), new ReplaceBlockConfiguration(overworld));
        register(context, NETHER_ORE_NODE_KEY, ModFeatures.ORE_NODE_FEATURE.get(), new ReplaceBlockConfiguration(nether));
    }

    @Nonnull
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, CreateReAutomated.asResource(name));
    }
}
