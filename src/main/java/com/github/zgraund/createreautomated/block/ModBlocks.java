package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateReAutomated.MOD_ID);

    public static final DeferredBlock<ExtractorBlock> EXTRACTOR = registerWithImpact(
            "extractor",
            ExtractorBlock::new,
            BlockBehaviour.Properties.of()
                                     .noOcclusion()
                                     .strength(1f)
                                     .isValidSpawn(Blocks::never)
                                     .mapColor(MapColor.COLOR_ORANGE)
                                     .sound(SoundType.METAL)
                                     .pushReaction(PushReaction.BLOCK),
            64.0
    );
    public static final DeferredBlock<OreNodeBlock> ORE_NODE = BLOCKS.registerBlock(
            "ore_node",
            OreNodeBlock::new,
            BlockBehaviour.Properties.of()
                                     .requiresCorrectToolForDrops()
                                     .sound(SoundType.STONE)
                                     .pushReaction(PushReaction.BLOCK)
                                     .strength(4f)
    );

    @Nonnull
    public static <T extends Block> DeferredBlock<T> registerWithImpact(String name, Function<BlockBehaviour.Properties, T> blockClass,
                                                                        BlockBehaviour.Properties properties, double stress) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockClass, properties);
        BlockStressValues.IMPACTS.registerProvider(block -> block == toReturn.get() ? () -> stress : null);
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
