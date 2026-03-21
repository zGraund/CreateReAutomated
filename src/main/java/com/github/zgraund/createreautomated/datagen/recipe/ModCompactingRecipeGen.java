package com.github.zgraund.createreautomated.datagen.recipe;

import com.github.zgraund.createreautomated.registry.ModItems;
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
public class ModCompactingRecipeGen extends CompactingRecipeGen {
    GeneratedRecipe DIAMOND = create(
            "bake_diamond",
            builder -> builder.require(ModItems.UNBAKED_DIAMOND).output(Items.DIAMOND).requiresHeat(HeatCondition.SUPERHEATED)
    );
    GeneratedRecipe
            RAW_IRON = compactBits(ModItems.IRON_BIT, Items.RAW_IRON, HeatCondition.NONE),
            RAW_COPPER = compactBits(ModItems.COPPER_BIT, Items.RAW_COPPER, HeatCondition.NONE),
            RAW_GOLD = compactBits(ModItems.GOLD_BIT, Items.RAW_GOLD, HeatCondition.HEATED);

    public ModCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    public GeneratedRecipe compactBits(ItemLike bit, @Nonnull ItemLike output, HeatCondition heat) {
        return create(
                BuiltInRegistries.ITEM.getKey(output.asItem()).getPath() + "_from_bits",
                builder -> {
                    for (int i = 0; i < 9; i++)
                        builder.require(bit);
                    return builder.output(output).requiresHeat(heat);
                }
        );
    }
}
