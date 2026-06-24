package com.github.zgraund.createreautomated.util;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreateRALang extends Lang {
    public static LangBuilder builder() {
        return new LangBuilder(CreateReAutomated.MOD_ID);
    }

    public static LangBuilder blockName(BlockState state) {
        return builder().add(state.getBlock()
                                  .getName());
    }

    public static LangBuilder itemName(ItemStack stack) {
        return builder().add(stack.getHoverName()
                                  .copy());
    }

    public static LangBuilder fluidName(FluidStack stack) {
        return builder().add(stack.getHoverName()
                                  .copy());
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }
}
