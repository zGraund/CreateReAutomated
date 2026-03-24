package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.datagen.ExtractingRecipeGen;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.github.zgraund.createreautomated.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ModExtractingRecipeGen extends ExtractingRecipeGen {
    GeneratedRecipe
            DIAMOND_BITS_EXTRA = create("diamond_bits_extra", builder ->
            defaultFrag(builder
                    .require(ModTags.Items.AT_LEAST_TIER_3)
                    .nodes(ModTags.Blocks.DIAMOND_NODES)
                    .secAtMaxSpeed(5)
                    .output(ModItems.DIAMOND_BIT, 2)
            )
    ),
            DIAMOND_BITS = create("diamond_bits", builder ->
                    defaultFrag(builder
                            .require(ModTags.Items.DRILL_TIER_2)
                            .nodes(ModTags.Blocks.DIAMOND_NODES)
                            .secAtMaxSpeed(10)
                            .output(ModItems.DIAMOND_BIT)
                    )
            ),
            GOLD_BITS = create("gold_bits", builder ->
                    defaultFrag(builder
                            .require(ModTags.Items.AT_LEAST_TIER_2)
                            .nodes(ModTags.Blocks.GOLD_NODES)
                            .secAtMaxSpeed(5)
                            .output(ModItems.GOLD_BIT)
                    )
            ),
            NETHER_GOLD_BITS = create("nether_gold_bits", builder ->
                    defaultFrag(builder
                            .require(ModTags.Items.AT_LEAST_TIER_2)
                            .nodes(ModBlocks.NETHER_GOLD_NODE)
                            .secAtMaxSpeed(10)
                            .output(ModItems.GOLD_BIT, 5)
                    )
            ),
            IRON_BITS = create("iron_bits", builder ->
                    defaultFrag(builder
                            .require(ModTags.Items.AT_LEAST_TIER_1)
                            .nodes(ModTags.Blocks.IRON_NODES)
                            .secAtMaxSpeed(5)
                            .output(ModItems.IRON_BIT, 3)
                    )
            ),
            COPPER_BITS = create("copper_bits", builder ->
                    defaultFrag(builder
                            .require(ModTags.Items.AT_LEAST_TIER_1)
                            .nodes(ModTags.Blocks.COPPER_NODES)
                            .secAtMaxSpeed(5)
                            .output(ModItems.IRON_BIT, 6)
                    )
            );

    public ModExtractingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateReAutomated.MOD_ID);
    }
}
