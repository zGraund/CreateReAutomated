package com.github.zgraund.createreautomated;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static class Client {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        public static final ModConfigSpec.BooleanValue DEBUG_ORE_NODE_DISPLAY = BUILDER
                .comment("Whether to display debug information when an Ore Node is right-clicked.")
                .define("debugOreNode", false);
        public static final ModConfigSpec.BooleanValue DEBUG_EXTRACTOR_INFO = BUILDER
                .comment("Whether to display debug information when looking at an extractor with the Engineer's Goggles equipped.")
                .define("debugExtractor", false);

        static final ModConfigSpec SPEC = BUILDER.build();
    }

    public static class Common {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        public static final ModConfigSpec.BooleanValue WORLD_GENERATION = BUILDER
                .comment("Whether Create-Re Automated should enable it's world generation")
                .define("worldGen", true);

        static final ModConfigSpec SPEC = BUILDER.build();
    }
}
