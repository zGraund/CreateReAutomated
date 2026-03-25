package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ModMechanicalCraftingRecipeGen extends MechanicalCraftingRecipeGen {
    GeneratedRecipe
            INFINITE_DIAMOND_NODE = defaultInfiniteNode(ModBlocks.INFINITE_DIAMOND_NODE, ModBlocks.DIAMOND_NODE),
            INFINITE_GOLD_NODE = defaultInfiniteNode(ModBlocks.INFINITE_GOLD_NODE, ModBlocks.GOLD_NODE),
            INFINITE_IRON_NODE = defaultInfiniteNode(ModBlocks.INFINITE_IRON_NODE, ModBlocks.IRON_NODE),
            INFINITE_COPPER_NODE = defaultInfiniteNode(ModBlocks.INFINITE_COPPER_NODE, ModBlocks.COPPER_NODE);

    public ModMechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateReAutomated.MOD_ID);
    }

    private GeneratedRecipe defaultInfiniteNode(ItemLike output, ItemLike core) {
        return create(() -> output).recipe(builder ->
                builder.key('A', ModItems.NODE_FRAGMENT)
                       .key('B', core)
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
