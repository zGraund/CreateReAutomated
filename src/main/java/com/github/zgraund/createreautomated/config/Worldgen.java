package com.github.zgraund.createreautomated.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class Worldgen extends ConfigBase {
    public final ConfigBool enabled =
            b(true, "enabled", "Whether Create Re-Automated should generate nodes in the world.");
    public final ConfigInt requiredFaces =
            i(3, 0, 6, "requiredFaces", "Number of faces that must touch the respective ore for a node to spawn");
    public final DimensionConfig overworld = nested(1, DimensionConfig::overworld, "Overworld specific configs.");
    public final DimensionConfig nether = nested(1, DimensionConfig::nether, "Nether specific configs.");

    @Override
    public String getName() {
        return "worldgen";
    }

    public DimensionConfig getConfig(ResourceKey<Level> key) {
        if (key == Level.OVERWORLD) return overworld;
        if (key == Level.NETHER) return nether;
        throw new IllegalStateException("Dimension type: " + key + " not supported in current version config.");
    }

    public static class DimensionConfig extends ConfigBase {
        public final ResourceKey<Level> dimension;
        public final ConfigInt tries;
        public final ConfigInt minY;
        public final ConfigInt maxY;

        public DimensionConfig(ResourceKey<Level> dimension, int tries, int minY, int maxY) {
            this.dimension = dimension;
            this.tries = i(tries, 0, 512, "tries", "Attempts per chunk to spawn a Node.");
            this.minY = i(minY, "fromY", "Max Y level where nodes can spawn.");
            this.maxY = i(maxY, "toY", "Min Y level where nodes can spawn.");
        }

        public static DimensionConfig overworld() {
            return new DimensionConfig(Level.OVERWORLD, 35, -64, 64);
        }

        public static DimensionConfig nether() {
            return new DimensionConfig(Level.NETHER, 50, 0, 128);
        }

        @Override
        public String getName() {
            return dimension.location().getPath();
        }
    }
}
