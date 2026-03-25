package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

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
               .addIngredients(recipe.getDrill())
               .setBackground(getRenderedSlot(), -1, -1);
        builder.addInputSlot(35, 50)
               .addItemStacks(recipe.getNodesAsItemStacks())
               .setBackground(getRenderedSlot(), -1, -1);
        List<ProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (ProcessingOutput output : results) {
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * 19;
            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, 121 + xOffset, 42 + yOffset)
                    .setBackground(getRenderedSlot(output.getChance()), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    protected void draw(ExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 115, 25);
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 74);
        Block node = recipeSlotsView.getSlotViews()
                                    .get(1)
                                    .getDisplayedItemStack()
                                    .map(ItemStack::getItem)
                                    .filter(BlockItem.class::isInstance)
                                    .map(item -> ((BlockItem) item).getBlock())
                                    .orElse(Blocks.AIR);
        Item drill = recipeSlotsView.getSlotViews()
                                    .get(0)
                                    .getDisplayedItemStack()
                                    .map(ItemStack::getItem)
                                    .orElse(Items.AIR);
        extractor.draw(graphics, 72, 56, node, drill);
    }
}
