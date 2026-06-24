package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;

import javax.annotation.Nonnull;

public class Server extends ConfigBase {
    public final ExtractorConfig extractorConfig = nested(1, ExtractorConfig::new, "Configure extractors stats.");
    public final NodeYields nodeYields = nested(1, NodeYields::new, "Configure each node extraction limit.");
    public final RecipeModifiers recipeModifiers = nested(
            1,
            RecipeModifiers::new,
            "Configure global recipe modifiers.",
            "The modifiers are multipliers that are applied to all Extracting recipes.",
            " > 0.5 = 50%",
            " > 2.0 = 200%"
    );

    @Nonnull
    @Override
    public String getName() {
        return "server";
    }
}
