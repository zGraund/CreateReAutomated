package com.github.zgraund.createreautomated.api.datagen;

import com.github.zgraund.createreautomated.recipe.ExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipeParams;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ExtractingRecipeGen extends ProcessingRecipeGen<ExtractingRecipeParams, ExtractingRecipe, ExtractingRecipe.Builder> {
    public ExtractingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    protected ExtractingRecipe.Builder defaultFrag(@Nonnull ExtractingRecipe.Builder builder) {
        return builder.output(0.01f, ModItems.NODE_FRAGMENT, 1);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return ModRecipeTypes.EXTRACTING;
    }

    @Override
    protected ExtractingRecipe.Builder getBuilder(ResourceLocation id) {
        return new ExtractingRecipe.Builder(ExtractingRecipe::new, id);
    }
}
