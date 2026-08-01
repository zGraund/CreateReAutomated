package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.datagen.AdvancedExtractingRecipeGen;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.github.zgraund.createreautomated.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class ModAdvancedExtractingRecipeGen extends AdvancedExtractingRecipeGen {
    GeneratedRecipe
            NETHERITE_BITS_SLOW = create("netherite_bits_from_diamond_drill", builder ->
            builder.require(ModItems.NETHERITE_DRILL)
                   .require(Fluids.LAVA, 1)
                   .nodes(ModBlocks.ANCIENT_DEBRIS_NODE)
                   .secAtMaxSpeed(210)
                   .output(ModItems.NETHERITE_BIT, 2)
                   .fragments(1, 0.02f)
    ),
            NETHERITE_BITS_FAST = create("netherite_bits_from_netherite_drill", builder ->
                    builder.require(ModItems.DIAMOND_DRILL)
                           .require(Fluids.LAVA, 1)
                           .nodes(ModBlocks.ANCIENT_DEBRIS_NODE)
                           .secAtMaxSpeed(420)
                           .output(ModItems.NETHERITE_BIT, 2)
                           .fragments(1, 0.02f)
            ),
            DIAMOND_BITS_EXTRA_WATER = create("diamond_bits_drill_tier3", builder ->
                    builder.require(ModTags.Items.AT_LEAST_TIER_3)
                           .require(Fluids.WATER, 1)
                           .nodes(ModTags.Blocks.DIAMOND_NODES)
                           .secAtMaxSpeed(4)
                           .output(ModItems.DIAMOND_BIT, 4)
                           .defaultFragments()
            ),
            DIAMOND_BITS_WATER = create("diamond_bits", builder ->
                    builder.require(ModTags.Items.DRILL_TIER_2)
                           .require(Fluids.WATER, 1)
                           .nodes(ModTags.Blocks.DIAMOND_NODES)
                           .secAtMaxSpeed(8)
                           .output(ModItems.DIAMOND_BIT, 2)
                           .defaultFragments()
            ),
            GOLD_BITS_WATER = create("gold_bits", builder ->
                    builder.require(ModTags.Items.AT_LEAST_TIER_2)
                           .require(Fluids.WATER, 1)
                           .nodes(ModTags.Blocks.GOLD_NODES)
                           .secAtMaxSpeed(4)
                           .output(ModItems.GOLD_BIT, 4)
                           .defaultFragments()
            ),
            NETHER_GOLD_BITS_WATER = create("nether_gold_bits", builder ->
                    builder.require(ModTags.Items.AT_LEAST_TIER_2)
                           .require(Fluids.WATER, 1)
                           .nodes(ModBlocks.NETHER_GOLD_NODE)
                           .secAtMaxSpeed(8)
                           .output(ModItems.GOLD_BIT, 6)
                           .defaultFragments()
            ),
            IRON_BITS_WATER = create("iron_bits", builder ->
                    builder.require(ModTags.Items.AT_LEAST_TIER_1)
                           .require(Fluids.WATER, 1)
                           .nodes(ModTags.Blocks.IRON_NODES)
                           .secAtMaxSpeed(4)
                           .output(ModItems.IRON_BIT, 6)
                           .defaultFragments()
            ),
            COPPER_BITS_WATER = create("copper_bits", builder ->
                    builder.require(ModTags.Items.AT_LEAST_TIER_1)
                           .require(Fluids.WATER, 1)
                           .nodes(ModTags.Blocks.COPPER_NODES)
                           .secAtMaxSpeed(4)
                           .output(ModItems.COPPER_BIT, 8)
                           .defaultFragments()
            ),
            ZINC_BITS_WATER = create("zinc_bits", builder ->
                    builder.require(ModTags.Items.AT_LEAST_TIER_1)
                           .require(Fluids.WATER, 1)
                           .nodes(ModTags.Blocks.ZINC_NODES)
                           .secAtMaxSpeed(4)
                           .output(ModItems.ZINC_BIT, 8)
                           .defaultFragments()
            ),
            QUARTZ_BITS_LAVA = create("quartz_bits", builder ->
                    builder.require(ModTags.Items.AT_LEAST_TIER_1)
                           .require(Fluids.LAVA, 1)
                           .nodes(ModTags.Blocks.QUARTZ_NODES)
                           .secAtMaxSpeed(4)
                           .extract(1)
                           .durabilityCost(5)
                           .output(ModItems.QUARTZ_BIT, 8)
                           .defaultFragments()
            );

    public ModAdvancedExtractingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateReAutomated.MOD_ID);
    }
}
