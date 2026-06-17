package com.github.zgraund.createreautomated.recipe;

import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ExtractingRecipeInput implements RecipeInput {
    private final ExtractorBlockEntity extractor;

    @Nonnull
    public static ExtractingRecipeInput of(ExtractorBlockEntity be) {
        return new ExtractingRecipeInput(be);
    }

    public ExtractingRecipeInput(ExtractorBlockEntity extractor) {
        this.extractor = extractor;
    }

    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        return drill();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return extractor.failPreConditions();
    }

    public ItemStack drill() {
        return extractor.getDrill();
    }

    public BlockState node() {
        return extractor.getNode();
    }

    public BlockPos nodePos() {
        return extractor.getNodePosition();
    }

    public ExtractorBlockEntity extractor() {
        return extractor;
    }
}
