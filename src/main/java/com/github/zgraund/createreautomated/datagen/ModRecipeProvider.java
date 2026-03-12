package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.github.zgraund.createreautomated.registry.ModTags;
import com.simibubi.create.AllBlocks;
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
    private RecipeOutput output;

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@Nonnull RecipeOutput recipeOutput) {
        this.output = recipeOutput;

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

        drillRecipe(ModItems.NETHERITE_DRILL, Items.NETHERITE_INGOT);
        drillRecipe(ModItems.DIAMOND_DRILL, Items.DIAMOND);
        drillRecipe(ModItems.IRON_DRILL, Items.IRON_INGOT);

        bitsPacking(ModItems.DIAMOND_BIT, Items.DIAMOND);
        bitsPacking(ModItems.GOLD_BIT, Items.GOLD_NUGGET);
        bitsPacking(ModItems.IRON_BIT, Items.IRON_NUGGET);
        bitsPacking(ModItems.COPPER_BIT, AllItems.COPPER_NUGGET);

        simpleExtractorRecipe(
                Ingredient.of(ModItems.DIAMOND_DRILL),
                fromBlocks(ModBlocks.DIAMOND_NODE, ModBlocks.DEEPSLATE_DIAMOND_NODE),
                secAtMaxSpeed(10),
                1,
                new ItemStack(ModItems.DIAMOND_BIT.get()),
                "diamond_bit"
        );

        simpleExtractorRecipe(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(ModBlocks.GOLD_NODE, ModBlocks.DEEPSLATE_GOLD_NODE),
                secAtMaxSpeed(5),
                1,
                new ItemStack(ModItems.GOLD_BIT.get(), 1),
                "gold_bit"
        );

        simpleExtractorRecipe(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(ModBlocks.NETHER_GOLD_NODE),
                secAtMaxSpeed(10),
                1,
                new ItemStack(ModItems.GOLD_BIT.get(), 10),
                "nether_gold_bit"
        );

        simpleExtractorRecipe(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(
                        ModBlocks.IRON_NODE,
                        ModBlocks.DEEPSLATE_IRON_NODE
                ),
                secAtMaxSpeed(5),
                1,
                new ItemStack(ModItems.IRON_BIT.get(), 3),
                "iron_bit"
        );

        simpleExtractorRecipe(
                Ingredient.of(ModTags.Items.DRILL_TIER_2),
                fromBlocks(
                        ModBlocks.COPPER_NODE,
                        ModBlocks.DEEPSLATE_COPPER_NODE
                ),
                secAtMaxSpeed(5),
                1,
                new ItemStack(ModItems.COPPER_BIT.get(), 6),
                "copper_bit"
        );
    }

    @SuppressWarnings("deprecation")
    protected HolderSet.Named<Block> fromTag(TagKey<Block> tag) {
        return HolderSet.emptyNamed(BuiltInRegistries.BLOCK.holderOwner(), tag);
    }

    @Nonnull
    @SafeVarargs
    protected final HolderSet.Direct<Block> fromBlocks(DeferredBlock<? extends Block>... blocks) {
        return HolderSet.direct(blocks);
    }

    protected String idAsString(String path) {
        return CreateReAutomated.MOD_ID + ":" + path;
    }

    protected void bitsPacking(ItemLike unpacked, ItemLike packed) {
        String unpackedName = getItemName(unpacked);
        String packedName = getItemName(packed);
        nineBlockStorageRecipes(
                this.output,
                RecipeCategory.MISC,
                unpacked,
                RecipeCategory.MISC,
                packed,
                idAsString(packedName + "_from_bits"),
                packedName,
                idAsString(unpackedName),
                unpackedName
        );
    }

    protected int secAtMaxSpeed(int seconds) {
        return seconds * 256 * 20;
    }

    protected void drillRecipe(DeferredItem<Item> drill, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, drill)
                           .define('I', Items.IRON_INGOT)
                           .define('M', material)
                           .pattern(" I ")
                           .pattern("MIM")
                           .pattern(" M ")
                           .group(idAsString("drills"))
                           .unlockedBy(getHasName(material), has(material))
                           .save(this.output, CreateReAutomated.asResource(drill.getId().getPath()));
    }

    protected void simpleExtractorRecipe(Ingredient drill, HolderSet<Block> set, int durationTicks, int drillDamage, ItemStack result, String name) {
        new ExtractorRecipeBuilder(drill, set, durationTicks, drillDamage, result)
                .save(this.output, CreateReAutomated.asResource("extracting/" + name));
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
