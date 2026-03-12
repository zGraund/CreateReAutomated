package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtractorCategory extends CreateRecipeCategory<ExtractorRecipe> {
    private final AnimatedExtractor extractor = new AnimatedExtractor();

    public ExtractorCategory(Info<ExtractorRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, ExtractorRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(35, 30)
               .addIngredients(recipe.drill())
               .setBackground(getRenderedSlot(), -1, -1);
        builder.addInputSlot(35, 50)
               .addItemStacks(recipe.getNodes())
               .setBackground(getRenderedSlot(), -1, -1);
        builder.addOutputSlot(121, 50)
               .addItemStack(recipe.result())
               .setBackground(getRenderedSlot(), -1, -1);
    }

    @Override
    protected void draw(ExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 115, 30);
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 74);
        HolderSet<Block> nodes = recipe.nodeSet();
        int nodeInd = nodes.size() == 1 ? 0 : (int) ((AnimationTickHolder.getRenderTime() / 20) % nodes.size());
        extractor.draw(graphics, 72, 56, nodes.get(nodeInd).value());
    }
}
