package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static int secAtMaxSpeed(int seconds) {
        return seconds * 256 * 20;
    }

    @Override
    protected void buildRecipes(@Nonnull RecipeOutput recipeOutput) {
        new ExtractorRecipeBuilder(
                Ingredient.of(ModItems.DRILLHEAD),
                ModBlocks.DIAMOND_NODE.get(),
                secAtMaxSpeed(5),
                1,
                new ItemStack(Items.TRIAL_KEY)
        ).save(recipeOutput, CreateReAutomated.asResource("extract_key"));
        new ExtractorRecipeBuilder(
                Ingredient.of(ModItems.DRILLHEAD),
                ModBlocks.DEEPSLATE_DIAMOND_NODE.get(),
                secAtMaxSpeed(5),
                5,
                new ItemStack(Items.OMINOUS_TRIAL_KEY)
        ).save(recipeOutput, CreateReAutomated.asResource("extract_better_key"));
    }

    public record ExtractorRecipeBuilder(Ingredient drill, Block node, int durationTicks, int drillDamage, ItemStack result) implements RecipeBuilder {
        @Nonnull
        @Override
        public RecipeBuilder unlockedBy(@Nonnull String name, @Nonnull Criterion<?> criterion) {return this;}

        @Nonnull
        @Override
        public RecipeBuilder group(@Nullable String groupName) {return this;}

        @Nonnull
        @Override
        public Item getResult() {
            return result.getItem();
        }

        @Override
        public void save(@Nonnull RecipeOutput recipeOutput, @Nonnull ResourceLocation id) {
            ExtractorRecipe recipe = new ExtractorRecipe(drill, node, durationTicks, drillDamage, result);
            recipeOutput.accept(id, recipe, null);
        }
    }
}
