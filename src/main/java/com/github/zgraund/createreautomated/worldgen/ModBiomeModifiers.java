package com.github.zgraund.createreautomated.worldgen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nonnull;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_ORE_NODE = registerKey("add_ore_node");

    public static void bootstrap(@Nonnull BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderSet<Biome> isOverworld = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

        HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);

        context.register(
                ADD_ORE_NODE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        isOverworld,
                        HolderSet.direct(placedFeature.getOrThrow(ModPlacedFeatures.OVERWORLD_ORE_NODE_PLACED_KEY)),
                        GenerationStep.Decoration.UNDERGROUND_DECORATION
                )
        );
    }

    @Nonnull
    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, CreateReAutomated.asResource(name));
    }
}
