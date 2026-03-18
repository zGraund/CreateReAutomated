package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;

import javax.annotation.Nonnull;

public class Common extends ConfigBase {
    public final ConfigBool worldgen =
            b(true, "worldGen", "Whether Create Re-Automated should generate nodes in the world.");

    @Nonnull
    @Override
    public String getName() {
        return "common";
    }
}
