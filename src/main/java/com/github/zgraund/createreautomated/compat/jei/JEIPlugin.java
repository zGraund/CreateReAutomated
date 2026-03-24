package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModRecipes;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation ID = CreateReAutomated.asResource("jei_plugin");
    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        allCategories.clear();

        allCategories.add(
                new CreateRecipeCategory.Builder<>(ExtractorRecipe.class)
                        .addTypedRecipes(ModRecipes.EXTRACTOR_RECIPE)
                        .catalyst(() -> ModBlocks.EXTRACTOR)
                        .emptyBackground(178, 100)
                        .itemIcon(ModBlocks.EXTRACTOR)
                        .build(CreateReAutomated.asResource("extracting"), ExtractorCategory::new)
        );

        registration.addRecipeCategories(allCategories.toArray(new CreateRecipeCategory[0]));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        allCategories.forEach(category -> category.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(category -> category.registerCatalysts(registration));
    }
}
