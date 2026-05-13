package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;

import javax.annotation.Nonnull;

public class Common extends ConfigBase {
    public final Worldgen worldgen = nested(1, Worldgen::new, "Configure nodes spawn.");

    @Nonnull
    @Override
    public String getName() {
        return "common";
    }
}
