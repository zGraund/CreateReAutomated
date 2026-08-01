package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.config.Worldgen;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModFeatures;
import com.github.zgraund.createreautomated.worldgen.feature.EncasedNodeConfiguration;
import com.github.zgraund.createreautomated.worldgen.feature.EncasedNodeFeature;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.data.worldgen.features.FeatureUtils.register;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_NODE_KEY = registerKey("copper_node");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ZINC_NODE_KEY = registerKey("zinc_node");
    public static final ResourceKey<ConfiguredFeature<?, ?>> IRON_NODE_KEY = registerKey("iron_node");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GOLD_NODE_KEY = registerKey("gold_node");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIAMOND_NODE_KEY = registerKey("diamond_node");

    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_GOLD_NODE_KEY = registerKey("nether_gold_node");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_QUARTZ_NODE_KEY = registerKey("nether_quartz_node");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_DEBRIS_NODE_KEY = registerKey("ancient_debris_node");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        List<OreConfiguration.TargetBlockState> copper = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.COPPER_ORE), ModBlocks.COPPER_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_COPPER_ORE), ModBlocks.DEEPSLATE_COPPER_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> zinc = List.of(
                OreConfiguration.target(new BlockMatchTest(AllBlocks.ZINC_ORE.get()), ModBlocks.ZINC_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(AllBlocks.DEEPSLATE_ZINC_ORE.get()), ModBlocks.DEEPSLATE_ZINC_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> iron = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.IRON_ORE), ModBlocks.IRON_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_IRON_ORE), ModBlocks.DEEPSLATE_IRON_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> gold = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.GOLD_ORE), ModBlocks.GOLD_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_GOLD_ORE), ModBlocks.DEEPSLATE_GOLD_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> diamond = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.DIAMOND_ORE), ModBlocks.DIAMOND_NODE.get().unstable()),
                OreConfiguration.target(new BlockMatchTest(Blocks.DEEPSLATE_DIAMOND_ORE), ModBlocks.DEEPSLATE_DIAMOND_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> netherGold = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.NETHER_GOLD_ORE), ModBlocks.NETHER_GOLD_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> netherQuartz = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.NETHER_QUARTZ_ORE), ModBlocks.NETHER_QUARTZ_NODE.get().unstable())
        );

        List<OreConfiguration.TargetBlockState> ancientDebris = List.of(
                OreConfiguration.target(new BlockMatchTest(Blocks.ANCIENT_DEBRIS), ModBlocks.ANCIENT_DEBRIS_NODE.get().unstable())
        );

        EncasedNodeFeature nodeFeature = ModFeatures.ENCASED_NODE_FEATURE.get();

        register(context, COPPER_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(copper, Worldgen.NodeGroup.COPPER));
        register(context, ZINC_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(zinc, Worldgen.NodeGroup.ZINC));
        register(context, IRON_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(iron, Worldgen.NodeGroup.IRON));
        register(context, GOLD_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(gold, Worldgen.NodeGroup.GOLD));
        register(context, DIAMOND_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(diamond, Worldgen.NodeGroup.DIAMOND));

        register(context, NETHER_GOLD_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(netherGold, Worldgen.NodeGroup.NETHER_GOLD));
        register(context, NETHER_QUARTZ_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(netherQuartz, Worldgen.NodeGroup.NETHER_QUARTZ));

        register(context, ANCIENT_DEBRIS_NODE_KEY, nodeFeature, EncasedNodeConfiguration.of(ancientDebris, Worldgen.NodeGroup.ANCIENT_DEBRIS));
    }

    @Nonnull
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, CreateReAutomated.asResource(name));
    }
}
