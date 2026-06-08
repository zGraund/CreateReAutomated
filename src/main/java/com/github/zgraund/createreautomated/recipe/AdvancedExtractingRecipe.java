package com.github.zgraund.createreautomated.recipe;

import com.github.zgraund.createreautomated.block.advancedextractor.AdvancedExtractorBlockEntity;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AdvancedExtractingRecipe extends ExtractingRecipe {
    public AdvancedExtractingRecipe(ExtractingRecipeParams params) {
        super(ModRecipeTypes.ADVANCED_EXTRACTING, params);
    }

    @Override
    public boolean matches(ExtractingRecipeInput input, Level level) {
        if (!(input.extractor() instanceof AdvancedExtractorBlockEntity extractor))
            return false;
        // check for modified amount manually because FluidIngredient is immutable
        if (getFluidAmountModified() > extractor.getFluid().getAmount())
            return false;
        return getFluid().test(extractor.getFluid()) && super.matches(input, level);
    }

    public SizedFluidIngredient getFluid() {
        if (fluidIngredients.isEmpty())
            throw new IllegalStateException("Advanced extracting recipe has no fluid!");
        return fluidIngredients.getFirst();
    }

    public int getFluidAmountModified() {
        return applyModifier(getFluid().amount(), MODIFIERS.fluid.getF());
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }
}
