package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.datagen.AdvancedExtractingRecipeGen;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModItems;
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
            );

    public ModAdvancedExtractingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateReAutomated.MOD_ID);
    }
}
