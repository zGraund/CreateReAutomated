package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class ModCompactingRecipeGen extends CompactingRecipeGen {
    GeneratedRecipe DIAMOND = create(
            "bake_diamond",
            builder -> builder.require(ModItems.UNBAKED_DIAMOND).output(Items.DIAMOND).requiresHeat(HeatCondition.SUPERHEATED)
    );
    GeneratedRecipe
            RAW_COPPER = compactBits(ModItems.COPPER_BIT, Items.RAW_COPPER, HeatCondition.NONE),
            RAW_ZINC = compactBits(ModItems.ZINC_BIT, AllItems.RAW_ZINC, HeatCondition.NONE),
            RAW_IRON = compactBits(ModItems.IRON_BIT, Items.RAW_IRON, HeatCondition.NONE),
            RAW_GOLD = compactBits(ModItems.GOLD_BIT, Items.RAW_GOLD, HeatCondition.HEATED);

    GeneratedRecipe compactBits(ItemLike bit, @Nonnull ItemLike output, HeatCondition heat) {
        return create(
                BuiltInRegistries.ITEM.getKey(output.asItem()).getPath() + "_from_bits",
                builder -> {
                    for (int i = 0; i < 9; i++)
                        builder.require(bit);
                    return builder.output(output).requiresHeat(heat);
                }
        );
    }

    public ModCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateReAutomated.MOD_ID);
    }
}
