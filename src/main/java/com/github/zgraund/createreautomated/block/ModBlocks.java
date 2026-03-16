package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.OreNodeBlockIndex;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeHolder;
import com.github.zgraund.createreautomated.item.ModItems;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateReAutomated.MOD_ID);
    private static final List<OreNodeHolder> ALL_NODES = new ArrayList<>();

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

    public static final DeferredBlock<OreNodeBlock> COPPER_NODE =
            stoneNode("copper_node", 100, List.of(Tags.Blocks.ORES_COPPER), List.of(new BlockMatchTest(Blocks.COPPER_ORE)));
    public static final DeferredBlock<OreNodeBlock> IRON_NODE =
            stoneNode("iron_node", 200, List.of(Tags.Blocks.ORES_IRON), List.of(new BlockMatchTest(Blocks.IRON_ORE)));
    public static final DeferredBlock<OreNodeBlock> GOLD_NODE =
            stoneNode("gold_node", 50, List.of(Tags.Blocks.ORES_GOLD), List.of(new BlockMatchTest(Blocks.GOLD_ORE)));
    public static final DeferredBlock<OreNodeBlock> DIAMOND_NODE =
            stoneNode("diamond_node", 20, List.of(Tags.Blocks.ORES_DIAMOND), List.of(new BlockMatchTest(Blocks.DIAMOND_ORE)));

    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_COPPER_NODE =
            deepslateNode("deepslate_copper_node", 150, List.of(Tags.Blocks.ORES_COPPER), List.of(new BlockMatchTest(Blocks.DEEPSLATE_COPPER_ORE)));
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_IRON_NODE =
            deepslateNode("deepslate_iron_node", 250, List.of(Tags.Blocks.ORES_IRON), List.of(new BlockMatchTest(Blocks.DEEPSLATE_IRON_ORE)));
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_GOLD_NODE =
            deepslateNode("deepslate_gold_node", 60, List.of(Tags.Blocks.ORES_GOLD), List.of(new BlockMatchTest(Blocks.DEEPSLATE_GOLD_ORE)));
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_DIAMOND_NODE =
            deepslateNode("deepslate_diamond_node", 20, List.of(Tags.Blocks.ORES_DIAMOND), List.of(new BlockMatchTest(Blocks.DEEPSLATE_DIAMOND_ORE)));

    public static final DeferredBlock<OreNodeBlock> NETHER_GOLD_NODE =
            node(
                    "nether_gold_node",
                    200,
                    Blocks.NETHERRACK,
                    List.of(Tags.Blocks.ORES_GOLD),
                    List.of(new BlockMatchTest(Blocks.NETHER_GOLD_ORE)),
                    netherrackNodeProperties()
            );

    public static DeferredBlock<OreNodeBlock> defaultNode(String name) {
        return node(name, 0, Blocks.AIR, List.of(), List.of(), stoneNodeProperties());
    }

    public static DeferredBlock<OreNodeBlock> stoneNode(String name, int limit, List<TagKey<Block>> tags, List<RuleTest> rules) {
        return node(name, limit, Blocks.COBBLESTONE, tags, rules, stoneNodeProperties());
    }

    public static DeferredBlock<OreNodeBlock> deepslateNode(String name, int limit, List<TagKey<Block>> tags, List<RuleTest> rules) {
        return node(name, limit, Blocks.COBBLED_DEEPSLATE, tags, rules, deepslateNodeProperties());
    }

    public static DeferredBlock<OreNodeBlock> node(
            String name, int limit, Block turnsInto, List<TagKey<Block>> tags, List<RuleTest> rules, BlockBehaviour.Properties properties
    ) {
        DeferredBlock<OreNodeBlock> block = BLOCKS.register(name, () -> new OreNodeBlock(properties, limit, turnsInto.defaultBlockState()));
        ModItems.ITEMS.registerSimpleBlockItem(name, block, ModItems.defaultNodeItemProperties());
        ALL_NODES.add(new OreNodeHolder(block, tags, rules));
        OreNodeBlockIndex.register(block);
        return block;
    }

    public static BlockBehaviour.Properties netherrackNodeProperties() {
        return stoneNodeProperties().sound(SoundType.NETHERRACK).strength(4f);
    }

    public static BlockBehaviour.Properties deepslateNodeProperties() {
        return stoneNodeProperties().sound(SoundType.DEEPSLATE).strength(6f);
    }

    public static BlockBehaviour.Properties stoneNodeProperties() {
        return BlockBehaviour.Properties.of()
                                        .requiresCorrectToolForDrops()
                                        .lightLevel(value -> value.getValue(OreNodeBlock.DEPLETION).getLightLevel())
                                        .sound(SoundType.STONE)
                                        .pushReaction(PushReaction.BLOCK)
                                        .strength(5f);
    }

    public static <T extends Block> DeferredBlock<T> registerWithImpact(String name, Function<BlockBehaviour.Properties, T> blockClass,
                                                                        BlockBehaviour.Properties properties, double stress) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockClass, properties);
        // Create mod block stress registry
        BlockStressValues.IMPACTS.registerProvider(block -> block == toReturn.get() ? () -> stress : null);
        return toReturn;
    }

    @Contract(pure = true)
    public static @UnmodifiableView List<OreNodeHolder> getAllNodes() {
        return Collections.unmodifiableList(ALL_NODES);
    }

    public static OreNodeBlock[] nodeBlocksArray() {
        return ALL_NODES.stream().map(OreNodeHolder::block).map(DeferredHolder::get).toArray(OreNodeBlock[]::new);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
