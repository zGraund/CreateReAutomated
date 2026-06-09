package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.recipe.AdvancedExtractingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AdvancedExtractingCategory extends ExtractingCategory<AdvancedExtractingRecipe> {
    public AdvancedExtractingCategory(Info<AdvancedExtractingRecipe> info) {
        super(info);
        this.extractor = new AnimatedAdvancedExtractor();
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, AdvancedExtractingRecipe recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        IRecipeSlotBuilder fluidSlot = builder.addInputSlot(35, 10).setBackground(getRenderedSlot(), -1, -1);
        for (FluidStack fluid : recipe.getFluid().getFluids())
            fluidSlot.addFluidStack(fluid.getFluid());
    }

    @Override
    protected void draw(AdvancedExtractingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        FluidStack fluid = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT)
                                          .getLast()
                                          .getDisplayedIngredient(NeoForgeTypes.FLUID_STACK)
                                          .orElse(FluidStack.EMPTY);
        ((AnimatedAdvancedExtractor) extractor).setFluid(fluid).draw(graphics, 72, 56);
    }
}
