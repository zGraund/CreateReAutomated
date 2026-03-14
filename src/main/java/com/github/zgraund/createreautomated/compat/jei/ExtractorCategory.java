package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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
//               .setSlotName("node")
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
