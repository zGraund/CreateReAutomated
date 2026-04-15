package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModMechanicalCraftingRecipeGen extends MechanicalCraftingRecipeGen {
    GeneratedRecipe
            INFINITE_DIAMOND_NODE = defaultInfiniteNode(ModBlocks.INFINITE_DIAMOND_NODE, ModBlocks.DIAMOND_NODE, ModBlocks.DEEPSLATE_DIAMOND_NODE),
            INFINITE_GOLD_NODE = defaultInfiniteNode(ModBlocks.INFINITE_GOLD_NODE, ModBlocks.GOLD_NODE, ModBlocks.DEEPSLATE_GOLD_NODE),
            INFINITE_IRON_NODE = defaultInfiniteNode(ModBlocks.INFINITE_IRON_NODE, ModBlocks.IRON_NODE, ModBlocks.DEEPSLATE_IRON_NODE),
            INFINITE_COPPER_NODE = defaultInfiniteNode(ModBlocks.INFINITE_COPPER_NODE, ModBlocks.COPPER_NODE, ModBlocks.DEEPSLATE_COPPER_NODE);

    public ModMechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateReAutomated.MOD_ID);
    }

    private GeneratedRecipe defaultInfiniteNode(@Nonnull Supplier<? extends ItemLike> output, ItemLike... nodes) {
        return create(output::get).recipe(builder ->
                builder.key('A', ModItems.NODE_FRAGMENT)
                       .key('B', Ingredient.of(nodes))
                       .key('C', Blocks.HEAVY_CORE)
                       .key('D', Items.ECHO_SHARD)
                       .patternLine(" AAA ")
                       .patternLine("ABDBA")
                       .patternLine("ADCDA")
                       .patternLine("ABDBA")
                       .patternLine(" AAA ")
                       .disallowMirrored()
        );
    }
}
