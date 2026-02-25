package com.github.zgraund.createreautomated;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DEBUG_ORE_NODE_DISPLAY = BUILDER
            .comment("Whether to display debug information when an Ore Node is right-clicked")
            .define("debugOreNode", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
