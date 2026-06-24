package com.github.zgraund.createreautomated.api.datagen;

import com.github.zgraund.createreautomated.recipe.AdvancedExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipeParams;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public abstract class AdvancedExtractingRecipeGen extends ProcessingRecipeGen<ExtractingRecipeParams, AdvancedExtractingRecipe,
        ExtractingRecipe.Builder<AdvancedExtractingRecipe>> {
    public AdvancedExtractingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return ModRecipeTypes.ADVANCED_EXTRACTING;
    }

    @Override
    protected ExtractingRecipe.Builder<AdvancedExtractingRecipe> getBuilder(ResourceLocation id) {
        return new ExtractingRecipe.Builder<>(AdvancedExtractingRecipe::new, id);
    }
}
