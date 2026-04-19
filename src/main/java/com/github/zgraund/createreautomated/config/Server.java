package com.github.zgraund.createreautomated.config;

import com.github.zgraund.createreautomated.registry.ModBlocks;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.function.DoubleSupplier;

public class Server extends ConfigBase {
    public final ConfigBool useDrillDurability = b(
            true,
            "useDrillDurability",
            "When True the Extractor will consume the durability of the Drill when mining."
    );
    public final ConfigFloat extractorImpact = f(
            64,
            0,
            "extractorImpact",
            "[in Stress Units]",
            "How much stress impact does the extractor generate. Note that this cost is doubled for every speed increase it receives."
    );

    public final NodeYields nodeYields = nested(1, NodeYields::new, "Configure each node extraction limit.");

    public DoubleSupplier getExtractorImpact(Block block) {
        return block == ModBlocks.EXTRACTOR.get() ? extractorImpact::get : null;
    }

    @Nonnull
    @Override
    public String getName() {
        return "server";
    }
}
