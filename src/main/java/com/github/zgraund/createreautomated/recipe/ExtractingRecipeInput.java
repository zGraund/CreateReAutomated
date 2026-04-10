package com.github.zgraund.createreautomated.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.Nonnull;

public record ExtractingRecipeInput(ItemStack drill, BlockPos nodePos) implements RecipeInput {
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
