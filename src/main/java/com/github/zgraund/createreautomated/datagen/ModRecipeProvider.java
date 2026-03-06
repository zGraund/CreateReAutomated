package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.github.zgraund.createreautomated.registry.ModTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;

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

    public static void drillRecipe(RecipeOutput recipeOutput, DeferredItem<Item> drill, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, drill)
                           .define('I', Items.IRON_INGOT)
                           .define('M', material)
                           .pattern(" I ")
                           .pattern("MIM")
                           .pattern(" M ")
                           .group("drills")
                           .unlockedBy("has_material", has(material))
                           .save(recipeOutput, CreateReAutomated.asResource(drill.getId().getPath()));
    }

    @Override
    protected void buildRecipes(@Nonnull RecipeOutput recipeOutput) {
        drillRecipe(recipeOutput, ModItems.STONE_DRILL, Items.COBBLESTONE);
        drillRecipe(recipeOutput, ModItems.COPPER_DRILL, Items.COPPER_INGOT);
        drillRecipe(recipeOutput, ModItems.IRON_DRILL, Items.IRON_INGOT);
        drillRecipe(recipeOutput, ModItems.DIAMOND_DRILL, Items.DIAMOND);

        new ExtractorRecipeBuilder(
                Ingredient.of(ModTags.Items.DRILL_TIER_1),
                ModBlocks.DIAMOND_NODE.get(),
                secAtMaxSpeed(5),
                1,
                new ItemStack(Items.TRIAL_KEY)
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/extract_key"));
        new ExtractorRecipeBuilder(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                ModBlocks.DEEPSLATE_DIAMOND_NODE.get(),
                secAtMaxSpeed(5),
                5,
                new ItemStack(Items.OMINOUS_TRIAL_KEY)
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/extract_better_key"));
        new ExtractorRecipeBuilder(
                Ingredient.of(ModItems.DIAMOND_DRILL),
                ModBlocks.NETHER_GOLD_NODE.get(),
                secAtMaxSpeed(5),
                5,
                new ItemStack(Items.GOLD_NUGGET, 10)
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/gold_nuggets"));
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
