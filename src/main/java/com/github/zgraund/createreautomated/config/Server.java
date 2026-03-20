package com.github.zgraund.createreautomated.config;

import com.github.zgraund.createreautomated.registry.ModBlocks;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.function.DoubleSupplier;

public class Server extends ConfigBase {
    public final ConfigFloat extractorStress = f(
            64,
            0,
            "extractorStress",
            "[in Stress Units]",
            "How much stress impact does the extractor generate. Note that this cost is doubled for every speed increase it receives."
    );

    public final NodeValues nodeValues = nested(1, NodeValues::new, "Configure each node extraction limit.");

    public DoubleSupplier getExtractorImpact(Block block) {
        return block == ModBlocks.EXTRACTOR.get() ? extractorStress::get : null;
    }

    @Nonnull
    @Override
    public String getName() {
        return "server";
    }

}
