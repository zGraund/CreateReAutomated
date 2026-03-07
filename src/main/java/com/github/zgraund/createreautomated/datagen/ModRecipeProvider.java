package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.github.zgraund.createreautomated.registry.ModTags;
import com.simibubi.create.AllItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@Nonnull RecipeOutput recipeOutput) {
        drillRecipe(recipeOutput, ModItems.STONE_DRILL, Items.COBBLESTONE);
        drillRecipe(recipeOutput, ModItems.COPPER_DRILL, Items.COPPER_INGOT);
        drillRecipe(recipeOutput, ModItems.IRON_DRILL, Items.IRON_INGOT);
        drillRecipe(recipeOutput, ModItems.DIAMOND_DRILL, Items.DIAMOND);
        // TODO: give names
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, ModItems.DIAMOND_BIT, RecipeCategory.MISC, Items.DIAMOND);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, ModItems.GOLD_BIT, RecipeCategory.MISC, Items.GOLD_NUGGET);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, ModItems.IRON_BIT, RecipeCategory.MISC, Items.IRON_NUGGET);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, ModItems.COPPER_BIT, RecipeCategory.MISC, AllItems.COPPER_NUGGET);

        new ExtractorRecipeBuilder(
                Ingredient.of(ModItems.DIAMOND_DRILL),
                fromBlocks(
                        ModBlocks.DIAMOND_NODE,
                        ModBlocks.DEEPSLATE_DIAMOND_NODE
                ),
                secAtMaxSpeed(10),
                1,
                new ItemStack(ModItems.DIAMOND_BIT.get())
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/diamond_bit"));
//        new ExtractorRecipeBuilder(
//                Ingredient.of(ModItems.DIAMOND_DRILL),
//                ModBlocks.DEEPSLATE_DIAMOND_NODE.get(),
//                secAtMaxSpeed(10),
//                1,
//                new ItemStack(ModItems.DIAMOND_BIT.get())
//        ).save(recipeOutput, CreateReAutomated.asResource("extracting/diamond_bit_deepslate"));

        new ExtractorRecipeBuilder(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(
                        ModBlocks.GOLD_NODE,
                        ModBlocks.DEEPSLATE_GOLD_NODE
                ),
                secAtMaxSpeed(5),
                1,
                new ItemStack(ModItems.GOLD_BIT.get(), 1)
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/gold_bit"));
//        new ExtractorRecipeBuilder(
//                Ingredient.of(ModTags.Items.DRILL_TIER_2),
//                ModBlocks.DEEPSLATE_GOLD_NODE.get(),
//                secAtMaxSpeed(5),
//                1,
//                new ItemStack(ModItems.GOLD_BIT.get(), 1)
//        ).save(recipeOutput, CreateReAutomated.asResource("extracting/gold_bit_deepslate"));
        new ExtractorRecipeBuilder(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(ModBlocks.NETHER_GOLD_NODE),
                secAtMaxSpeed(10),
                1,
                new ItemStack(ModItems.GOLD_BIT.get(), 1)
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/nether_gold_bit"));

        new ExtractorRecipeBuilder(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(
                        ModBlocks.IRON_NODE,
                        ModBlocks.DEEPSLATE_IRON_NODE
                ),
                secAtMaxSpeed(5),
                1,
                new ItemStack(ModItems.IRON_BIT.get(), 3)
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/iron_bit"));
//        new ExtractorRecipeBuilder(
//                Ingredient.of(ModTags.Items.DRILL_TIER_2),
//                ModBlocks.DEEPSLATE_IRON_NODE.get(),
//                secAtMaxSpeed(5),
//                1,
//                new ItemStack(ModItems.IRON_BIT.get(), 3)
//        ).save(recipeOutput, CreateReAutomated.asResource("extracting/iron_bit_deepslate"));

        new ExtractorRecipeBuilder(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(
                        ModBlocks.COPPER_NODE,
                        ModBlocks.DEEPSLATE_COPPER_NODE
                ),
                secAtMaxSpeed(5),
                1,
                new ItemStack(ModItems.COPPER_BIT.get(), 6)
        ).save(recipeOutput, CreateReAutomated.asResource("extracting/copper_bit"));

//        new ExtractorRecipeBuilder(
//                Ingredient.of(ModTags.Items.DRILL_TIER_2),
//                fromTag(BlockTags.MINEABLE_WITH_PICKAXE),
//                secAtMaxSpeed(5),
//                1,
//                new ItemStack(ModItems.COPPER_BIT.get(), 6)
//        ).save(recipeOutput, CreateReAutomated.asResource("extracting/all_the_blocks"));
//        new ExtractorRecipeBuilder(
//                Ingredient.of(ModTags.Items.DRILL_TIER_2),
//                ModBlocks.DEEPSLATE_COPPER_NODE.get(),
//                secAtMaxSpeed(5),
//                1,
//                new ItemStack(ModItems.COPPER_BIT.get(), 6)
//        ).save(recipeOutput, CreateReAutomated.asResource("extracting/copper_bit_deepslate"));
    }

    @SuppressWarnings("deprecation")
    public HolderSet.Named<Block> fromTag(TagKey<Block> tag) {
        return HolderSet.emptyNamed(BuiltInRegistries.BLOCK.holderOwner(), tag);
    }

    @Nonnull
    @SafeVarargs
    public final HolderSet.Direct<Block> fromBlocks(DeferredBlock<? extends Block>... blocks) {
        return HolderSet.direct(blocks);
    }

    public int secAtMaxSpeed(int seconds) {
        return seconds * 256 * 20;
    }

    public void drillRecipe(RecipeOutput recipeOutput, DeferredItem<Item> drill, ItemLike material) {
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

    public record ExtractorRecipeBuilder(Ingredient drill, HolderSet<Block> node, int durationTicks, int drillDamage, ItemStack result) implements RecipeBuilder {
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
