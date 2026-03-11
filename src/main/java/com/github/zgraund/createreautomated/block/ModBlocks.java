package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeHolder;
import com.github.zgraund.createreautomated.item.ModItems;
import com.simibubi.create.api.stress.BlockStressValues;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
            registerDefaultNode("copper_node", 100, List.of(Tags.Blocks.ORES_COPPER), List.of(new BlockMatchTest(Blocks.COPPER_ORE)));
    public static final DeferredBlock<OreNodeBlock> IRON_NODE =
            registerDefaultNode("iron_node", 200, List.of(Tags.Blocks.ORES_IRON), List.of(new BlockMatchTest(Blocks.IRON_ORE)));
    public static final DeferredBlock<OreNodeBlock> GOLD_NODE =
            registerDefaultNode("gold_node", 50, List.of(Tags.Blocks.ORES_GOLD), List.of(new BlockMatchTest(Blocks.GOLD_ORE)));
    public static final DeferredBlock<OreNodeBlock> DIAMOND_NODE =
            registerDefaultNode("diamond_node", 20, List.of(Tags.Blocks.ORES_DIAMOND), List.of(new BlockMatchTest(Blocks.DIAMOND_ORE)));
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_COPPER_NODE =
            registerDefaultNode("deepslate_copper_node", 150, List.of(Tags.Blocks.ORES_COPPER), List.of(new BlockMatchTest(Blocks.DEEPSLATE_COPPER_ORE)));
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_IRON_NODE =
            registerDefaultNode("deepslate_iron_node", 250, List.of(Tags.Blocks.ORES_IRON), List.of(new BlockMatchTest(Blocks.DEEPSLATE_IRON_ORE)));
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_GOLD_NODE =
            registerDefaultNode("deepslate_gold_node", 60, List.of(Tags.Blocks.ORES_GOLD), List.of(new BlockMatchTest(Blocks.DEEPSLATE_GOLD_ORE)));
    public static final DeferredBlock<OreNodeBlock> DEEPSLATE_DIAMOND_NODE =
            registerDefaultNode("deepslate_diamond_node", 20, List.of(Tags.Blocks.ORES_DIAMOND), List.of(new BlockMatchTest(Blocks.DEEPSLATE_DIAMOND_ORE)));
    public static final DeferredBlock<OreNodeBlock> NETHER_GOLD_NODE =
            registerDefaultNode("nether_gold_node", 200, List.of(Tags.Blocks.ORES_GOLD), List.of(new BlockMatchTest(Blocks.NETHER_GOLD_ORE)));

    @Nonnull
    public static DeferredBlock<OreNodeBlock> registerDefaultNode(String name) {
        return registerDefaultNode(name, 0);
    }

    @Nonnull
    public static DeferredBlock<OreNodeBlock> registerDefaultNode(String name, int limit) {
        return registerDefaultNode(name, limit, List.of());
    }

    @Nonnull
    public static DeferredBlock<OreNodeBlock> registerDefaultNode(String name, int limit, List<TagKey<Block>> tags) {
        return registerDefaultNode(name, limit, tags, List.of());
    }

    @Nonnull
    public static DeferredBlock<OreNodeBlock> registerDefaultNode(String name, int limit, List<TagKey<Block>> tags, List<RuleTest> rules) {
        DeferredBlock<OreNodeBlock> block = BLOCKS.register(name, () -> new OreNodeBlock(defaultNodeProperties(), limit));
        ModItems.ITEMS.registerSimpleBlockItem(name, block, ModItems.defaultNodeItemProperties());
        ALL_NODES.add(new OreNodeHolder(block, tags, rules));
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
    @Contract(pure = true)
    public static @UnmodifiableView List<OreNodeHolder> getAllNodes() {
        return Collections.unmodifiableList(ALL_NODES);
    }

    @Nonnull
    public static OreNodeBlock[] nodeBlocksArray() {
        return ALL_NODES.stream().map(OreNodeHolder::block).map(DeferredHolder::get).toArray(OreNodeBlock[]::new);
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
