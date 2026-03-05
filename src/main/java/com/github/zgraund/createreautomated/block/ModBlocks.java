package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.item.ModItems;
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
    public static final DeferredBlock<OreNodeBlock> ORE_NODE = registerDefaultNode("ore_node");
    public static final DeferredBlock<OreNodeBlock> ORE_NODE_LIMITED = registerDefaultNode("ore_node_limited", 10);
    public static final DeferredBlock<OreNodeBlock> ORE_NODE_TEST = registerDefaultNode("ore_node_test", 10);

    @Nonnull
    public static DeferredBlock<OreNodeBlock> registerDefaultNode(String name) {
        return registerDefaultNode(name, 0);
    }

    @Nonnull
    public static DeferredBlock<OreNodeBlock> registerDefaultNode(String name, int limit) {
        DeferredBlock<OreNodeBlock> block = BLOCKS.register(name, () -> new OreNodeBlock(defaultNodeProperties(), limit));
        ModItems.ITEMS.registerSimpleBlockItem(name, block, ModItems.defaultNodeItemProperties());
        return block;
    }

    @Nonnull
    public static <T extends Block> DeferredBlock<T> registerWithImpact(String name, Function<BlockBehaviour.Properties, T> blockClass,
                                                                        BlockBehaviour.Properties properties, double stress) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockClass, properties);

        // Create block stress registry
        BlockStressValues.IMPACTS.registerProvider(block -> block == toReturn.get() ? () -> stress : null);

        return toReturn;
    }

    @Nonnull
    public static BlockBehaviour.Properties defaultNodeProperties() {
        return BlockBehaviour.Properties.of()
                                        .requiresCorrectToolForDrops()
                                        .lightLevel(value -> value.getValue(OreNodeBlock.DEPLETION).getLightLevel())
                                        .sound(SoundType.STONE)
                                        .pushReaction(PushReaction.BLOCK)
                                        .strength(4f);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
