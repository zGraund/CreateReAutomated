package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;

import javax.annotation.Nonnull;

public class RecipeModifiers extends ConfigBase {
    public final ConfigFloat duration = multi("duration", "Extract duration multiplier.");
    public final ConfigFloat durability = multi("durability", "Drill durability consumption multiplier.");
    public final ConfigFloat fluid = multi("fluid", "Fluid consumption multiplier.");
    public final ConfigFloat node = multi("nodeConsumption", "Node consumption multiplier.");
    public final ConfigFloat output = multi("output", "Item output multiplier.");

    public ConfigFloat multi(String name, String... comments) {
        return f(1, 0, 100, name, comments);
    }

    @Nonnull
    @Override
    public String getName() {
        return "recipeModifiers";
    }
}
