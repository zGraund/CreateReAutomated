package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateReAutomated.MOD_ID);

    public static final DeferredBlock<Block> EXTRACTOR = BLOCKS.registerBlock(
            "extractor",
            ExtractorBlock::new,
            BlockBehaviour.Properties.of().noOcclusion().strength(1f).sound(SoundType.WOOD).pushReaction(PushReaction.BLOCK)
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
