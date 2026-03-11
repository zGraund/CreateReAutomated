package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.node.OreNodeHolder;
import com.github.zgraund.createreautomated.registry.ModFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.data.worldgen.features.FeatureUtils.register;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_NODE_KEY = registerKey("ore_node");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // TODO: divide in group for rarity spawn?
        List<OreConfiguration.TargetBlockState> rules = ModBlocks.getAllNodes().stream().flatMap(OreNodeHolder::getRules).toList();

        register(context, OVERWORLD_ORE_NODE_KEY, ModFeatures.ORE_NODE_FEATURE.get(), new ReplaceBlockConfiguration(rules));
    }

    @Nonnull
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, CreateReAutomated.asResource(name));
    }
}
