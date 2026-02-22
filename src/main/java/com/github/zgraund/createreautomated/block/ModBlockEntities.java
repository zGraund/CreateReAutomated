package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateReAutomated.MOD_ID);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }    @SuppressWarnings("DataFlowIssue")
    public static final Supplier<BlockEntityType<ExtractorBlockEntity>> EXTRACTOR_BE = BLOCK_ENTITY_TYPES.register(
            "extractor_be",
            () -> BlockEntityType.Builder.of(ExtractorBlockEntity::new, ModBlocks.EXTRACTOR.get()).build(null)
    );


}
