package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class RecipeModifiers extends ConfigBase {
    public final ConfigFloat duration = multi("duration", "Extract duration multiplier.");
    public final ConfigFloat durability = multi("durability", "Drill durability consumption multiplier.");
    public final ConfigFloat fluid = multi("fluid", "Fluid consumption multiplier.");
    public final ConfigFloat node = multi("nodeConsumption", "Node consumption multiplier.");
    public final ConfigFloat output = multi("output", "Item output multiplier.");

    public static Component formatModifier(@Nonnull ConfigFloat mod) {
        return formatModifier(mod.getF());
    }

    public static Component formatModifier(float mod) {
        if (mod == 1)
            return Component.empty();
        return Component.literal(new DecimalFormat(" (###.###x)").format(mod)).withStyle(mod < 1f ? ChatFormatting.DARK_GREEN : ChatFormatting.RED);
    }

    public ConfigFloat multi(String name, String... comments) {
        return f(1, 0, 100, name, comments);
    }

    @Nonnull
    @Override
    public String getName() {
        return "recipeModifiers";
    }
}
