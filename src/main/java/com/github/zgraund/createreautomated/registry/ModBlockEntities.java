package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.OreNodeBlockIndex;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import com.github.zgraund.createreautomated.block.extractor.ExtractorRenderer;
import com.github.zgraund.createreautomated.block.node.OreNodeEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {
    private static final CreateRegistrate REGISTRATE = CreateReAutomated.REGISTRATE;

    public static final BlockEntityEntry<ExtractorBlockEntity> EXTRACTOR_BE =
            REGISTRATE.blockEntity("extractor_be", ExtractorBlockEntity::new)
                      .validBlocks(ModBlocks.EXTRACTOR)
                      .renderer(() -> ExtractorRenderer::new)
                      .register();

    public static final BlockEntityEntry<OreNodeEntity> ORE_NODE_BE =
            REGISTRATE.blockEntity("ore_node_be", OreNodeEntity::new)
                      .validBlocks(OreNodeBlockIndex.toArray())
                      .register();

    public static void register() {}
}
