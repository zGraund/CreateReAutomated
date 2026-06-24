package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;

import javax.annotation.Nonnull;

public class Client extends ConfigBase {
    public final ConfigBool nodeParticles = b(
            true,
            "nodeParticles",
            "Enable/disable unstable Ore Node particles."
    );
    public final ConfigBool advancedExtractorInfo = b(
            false,
            "advancedExtractorInfo",
            "Whether to display advanced information when looking at an extractor with the Engineer's Goggles equipped."
    );

    @Nonnull
    @Override
    public String getName() {
        return "client";
    }
}
