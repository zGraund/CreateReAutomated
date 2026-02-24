package com.github.zgraund.createreautomated.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public record ExtractorRecipeInput(ItemStack drill, BlockState node) implements RecipeInput {
    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        return drill;
    }

    @Override
    public int size() {
        return 1;
    }
}
