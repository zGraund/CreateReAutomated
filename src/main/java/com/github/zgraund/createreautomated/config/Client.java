package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;

import javax.annotation.Nonnull;

public class Client extends ConfigBase {
    public final ConfigBool debugOreNodeOverlay = b(
            false,
            "debugOreNode",
            "Whether to display debug information when an Ore Node is right-clicked."
    );
    public final ConfigBool debugExtractorOverlay = b(
            false,
            "debugExtractor",
            "Whether to display debug information when looking at an extractor with the Engineer's Goggles equipped."
    );

    @Nonnull
    @Override
    public String getName() {
        return "client";
    }
}
