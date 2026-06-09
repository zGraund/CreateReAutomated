package com.github.zgraund.createreautomated.config;

import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ExtractorConfig extends ConfigBase {
    public final ConfigBool useDrillDurability = b(
            true,
            "useDrillDurability",
            "When True the Extractor will consume the durability of the Drill when mining."
    );
    public final KineticConfig extractor = nested(1, KineticConfig::extractor);
    public final KineticConfig advancedExtractor = nested(1, KineticConfig::advancedExtractor);

    @Nullable
    public DoubleSupplier getExtractorImpact(Block block) {
        KineticConfig config = getForBlock(block);
        return config == null ? null : config.impact::get;
    }

    @Nullable
    public Supplier<IRotate.SpeedLevel> getExtractorSpeed(Block block) {
        KineticConfig config = getForBlock(block);
        return config == null ? null : config.speed::get;
    }

    @Nullable
    public KineticConfig getForBlock(Block block) {
        if (block == ModBlocks.EXTRACTOR.get()) return extractor;
        if (block == ModBlocks.ADVANCED_EXTRACTOR.get()) return advancedExtractor;
        return null;
    }

    @Nonnull
    @Override
    public String getName() {
        return "extractorConfig";
    }

    public static class KineticConfig extends ConfigBase {
        public final ConfigFloat impact;
        public final ConfigEnum<IRotate.SpeedLevel> speed;
        private final String name;

        public KineticConfig(@Nonnull BlockEntry<?> block, float impact, IRotate.SpeedLevel speed) {
            this.name = block.getId().getPath();
            this.impact = f(impact, 0, "impact", Comments.su);
            this.speed = e(speed, "speed", Comments.speed);
        }

        @Nonnull
        public static KineticConfig extractor() {
            return new KineticConfig(ModBlocks.EXTRACTOR, 64, IRotate.SpeedLevel.MEDIUM);
        }

        @Nonnull
        public static KineticConfig advancedExtractor() {
            return new KineticConfig(ModBlocks.ADVANCED_EXTRACTOR, 64, IRotate.SpeedLevel.MEDIUM);
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }
    }

    public static class Comments {
        public static final String su = "[in Stress Units]";
        public static final String speed = "The minimum speed required to power the block.";
    }
}
