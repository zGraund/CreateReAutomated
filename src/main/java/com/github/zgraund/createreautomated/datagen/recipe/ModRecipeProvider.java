package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@Nonnull RecipeOutput recipeOutput) {
        // Extractor
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.EXTRACTOR)
                           .unlockedBy("has_", has(AllBlocks.BRASS_CASING))
                           .define('I', AllItems.BRASS_INGOT)
                           .define('C', AllBlocks.BRASS_CASING)
                           .define('O', AllBlocks.COGWHEEL)
                           .define('E', AllItems.ELECTRON_TUBE)
                           .pattern("ICI")
                           .pattern("IOI")
                           .pattern("IEI")
                           .save(recipeOutput);

        // Drills
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_DRILL)
                           .unlockedBy("has_iron", has(Items.IRON_INGOT))
                           .unlockedBy("has_extractor", has(ModBlocks.EXTRACTOR))
                           .define('I', Items.IRON_INGOT)
                           .define('A', AllItems.ANDESITE_ALLOY)
                           .pattern(" A ")
                           .pattern("III")
                           .pattern(" I ")
                           .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIAMOND_DRILL)
                           .unlockedBy("has_diamond", has(Items.DIAMOND))
                           .unlockedBy("has_extractor", has(ModBlocks.EXTRACTOR))
                           .define('P', AllItems.PRECISION_MECHANISM)
                           .define('I', ModItems.IRON_DRILL)
                           .define('D', Items.DIAMOND)
                           .pattern(" P ")
                           .pattern("DID")
                           .pattern(" D ")
                           .save(recipeOutput);

        SmithingTransformRecipeBuilder.smithing(
                                              Ingredient.EMPTY,
                                              Ingredient.of(ModItems.DIAMOND_DRILL),
                                              Ingredient.of(Items.NETHERITE_INGOT),
                                              RecipeCategory.MISC,
                                              ModItems.NETHERITE_DRILL.get()
                                      )
                                      .unlocks("has_netherite", has(Items.NETHERITE_INGOT))
                                      .unlocks("has_extractor", has(ModBlocks.EXTRACTOR))
                                      .save(recipeOutput, CreateReAutomated.asResource("netherite_drill_upgrade"));

        // Node Stabilizer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STABILIZER_CAGE)
                           .define('F', ModItems.NODE_FRAGMENT)
                           .define('S', Items.NETHER_STAR)
                           .pattern("FFF")
                           .pattern("FSF")
                           .pattern("FFF")
                           .unlockedBy(getHasName(ModItems.NODE_FRAGMENT), has(ModItems.NODE_FRAGMENT))
                           .save(recipeOutput);

    }
}
