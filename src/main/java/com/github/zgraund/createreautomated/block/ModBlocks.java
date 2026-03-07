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
    public static final DeferredBlock<OreNodeBlock> DIAMOND_NODE = registerDefaultNode("diamond_node", 20);
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_DIAMOND_NODE = registerDefaultNode("deepslate_diamond_node", 20);
    public static final DeferredBlock<OreNodeBlock> GOLD_NODE = registerDefaultNode("gold_node", 50);
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_GOLD_NODE = registerDefaultNode("deepslate_gold_node", 60);
    public static final DeferredBlock<OreNodeBlock> IRON_NODE = registerDefaultNode("iron_node", 200);
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_IRON_NODE = registerDefaultNode("deepslate_iron_node", 250);
    public static final DeferredBlock<OreNodeBlock> COPPER_NODE = registerDefaultNode("copper_node", 100);
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_COPPER_NODE = registerDefaultNode("deepslate_copper_node", 150);
    public static final DeferredBlock<OreNodeBlock> NETHER_GOLD_NODE = registerDefaultNode("nether_gold_node", 200);

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
