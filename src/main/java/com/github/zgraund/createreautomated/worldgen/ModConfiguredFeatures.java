package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.data.worldgen.features.FeatureUtils.register;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_NODE_KEY = registerKey("ore_node");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // TODO: divide in group for rarity spawn?
        RuleTest diamondNode = new BlockMatchTest(Blocks.DIAMOND_ORE);
        RuleTest diamondNodeDeep = new BlockMatchTest(Blocks.DEEPSLATE_DIAMOND_ORE);

        RuleTest goldNode = new BlockMatchTest(Blocks.GOLD_ORE);
        RuleTest goldNodeDeep = new BlockMatchTest(Blocks.DEEPSLATE_GOLD_ORE);

        RuleTest ironNode = new BlockMatchTest(Blocks.IRON_ORE);
        RuleTest ironNodeDeep = new BlockMatchTest(Blocks.DEEPSLATE_IRON_ORE);

        RuleTest copperNode = new BlockMatchTest(Blocks.COPPER_ORE);
        RuleTest copperNodeDeep = new BlockMatchTest(Blocks.DEEPSLATE_COPPER_ORE);

        List<OreConfiguration.TargetBlockState> ironNodeList = List.of(
                OreConfiguration.target(diamondNode, ModBlocks.DIAMOND_NODE.get().natural()),
                OreConfiguration.target(diamondNodeDeep, ModBlocks.DEEPSLATE_DIAMOND_NODE.get().natural()),

                OreConfiguration.target(goldNode, ModBlocks.GOLD_NODE.get().natural()),
                OreConfiguration.target(goldNodeDeep, ModBlocks.DEEPSLATE_GOLD_NODE.get().natural()),

                OreConfiguration.target(ironNode, ModBlocks.IRON_NODE.get().natural()),
                OreConfiguration.target(ironNodeDeep, ModBlocks.DEEPSLATE_IRON_NODE.get().natural()),

                OreConfiguration.target(copperNode, ModBlocks.COPPER_NODE.get().natural()),
                OreConfiguration.target(copperNodeDeep, ModBlocks.DEEPSLATE_COPPER_NODE.get().natural())
        );

        register(context, OVERWORLD_ORE_NODE_KEY, Feature.REPLACE_SINGLE_BLOCK, new ReplaceBlockConfiguration(ironNodeList));
    }

    @Nonnull
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, CreateReAutomated.asResource(name));
    }
}
