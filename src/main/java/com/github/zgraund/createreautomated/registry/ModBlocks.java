package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.InfiniteNodeBlock;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.config.NodeYields;
import com.github.zgraund.createreautomated.datagen.ModBlockLootTableGen;
import com.github.zgraund.createreautomated.datagen.ModCommonBlockModelGen;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.Arrays;

@MethodsReturnNonnullByDefault
public class ModBlocks {
    private static final CreateRegistrate REGISTRATE = CreateReAutomated.REGISTRATE;

    public static final BlockEntry<ExtractorBlock> EXTRACTOR =
            REGISTRATE.block("extractor", ExtractorBlock::new)
                      .properties(properties ->
                              properties.noOcclusion()
                                        .strength(1f)
                                        .isValidSpawn(Blocks::never)
                                        .mapColor(MapColor.COLOR_ORANGE)
                                        .sound(SoundType.METAL)
                                        .pushReaction(PushReaction.BLOCK)
                      )
                      .loot((prov, block) ->
                              prov.add(block, ModBlockLootTableGen.createSinglePropConditionTable(block, ExtractorBlock.HALF, DoubleBlockHalf.LOWER))
                      )
                      .blockstate((ctx, prov) ->
                              BlockStateGen.simpleBlock(ctx, prov, state ->
                                      prov.models().getExistingFile(prov.modLoc("block/extractor/" + state.getValue(ExtractorBlock.HALF)))
                              )
                      )
                      .transform(TagGen.pickaxeOnly())
                      .item(DoubleHighBlockItem::new)
                      .transform(ModelGen.customItemModel())
                      .register();

    public static final BlockEntry<InfiniteNodeBlock>
            INFINITE_COPPER_NODE = infiniteNode("infinite_copper_node", ModTags.Blocks.COPPER_NODES),
            INFINITE_ZINC_NODE = infiniteNode("infinite_zinc_node", ModTags.Blocks.ZINC_NODES),
            INFINITE_IRON_NODE = infiniteNode("infinite_iron_node", ModTags.Blocks.IRON_NODES),
            INFINITE_GOLD_NODE = infiniteNode("infinite_gold_node", ModTags.Blocks.GOLD_NODES),
            INFINITE_DIAMOND_NODE = infiniteNode("infinite_diamond_node", ModTags.Blocks.DIAMOND_NODES);

    public static final BlockEntry<OreNodeBlock>
            COPPER_NODE = stoneNode("copper_node", 240, Tags.Blocks.ORES_COPPER, ModTags.Blocks.COPPER_NODES),
            ZINC_NODE = stoneNode("zinc_node", 200, CommonMetal.ZINC.ores.blocks(), CommonMetal.ZINC.ores.items(), ModTags.Blocks.ZINC_NODES),
            IRON_NODE = stoneNode("iron_node", 240, Tags.Blocks.ORES_IRON, ModTags.Blocks.IRON_NODES),
            GOLD_NODE = stoneNode("gold_node", 180, Tags.Blocks.ORES_GOLD, ModTags.Blocks.GOLD_NODES),
            DIAMOND_NODE = stoneNode("diamond_node", 108, Tags.Blocks.ORES_DIAMOND, ModTags.Blocks.DIAMOND_NODES);

    public static final BlockEntry<OreNodeBlock>
            DEEPSLATE_COPPER_NODE = deepslateNode("deepslate_copper_node", 300, Tags.Blocks.ORES_COPPER, ModTags.Blocks.COPPER_NODES),
            DEEPSLATE_ZINC_NODE = deepslateNode("deepslate_zinc_node", 250, CommonMetal.ZINC.ores.blocks(), CommonMetal.ZINC.ores.items(), ModTags.Blocks.ZINC_NODES),
            DEEPSLATE_IRON_NODE = deepslateNode("deepslate_iron_node", 300, Tags.Blocks.ORES_IRON, ModTags.Blocks.IRON_NODES),
            DEEPSLATE_GOLD_NODE = deepslateNode("deepslate_gold_node", 225, Tags.Blocks.ORES_GOLD, ModTags.Blocks.GOLD_NODES),
            DEEPSLATE_DIAMOND_NODE = deepslateNode("deepslate_diamond_node", 135, Tags.Blocks.ORES_DIAMOND, ModTags.Blocks.DIAMOND_NODES);

    public static final BlockEntry<OreNodeBlock>
            NETHER_GOLD_NODE = netherrackNode("nether_gold_node", 180, Tags.Blocks.ORES_GOLD);

    private static BlockEntry<OreNodeBlock> stoneNode(String name, int limit, TagKey<?>... tags) {
        return node(name, limit, Blocks.COBBLESTONE, stoneNodeProperties(),
                addTags(tags, Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE));
    }

    private static BlockEntry<OreNodeBlock> deepslateNode(String name, int limit, TagKey<?>... tags) {
        return node(name, limit, Blocks.COBBLED_DEEPSLATE, deepslateNodeProperties(),
                addTags(tags, Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE));
    }

    private static BlockEntry<OreNodeBlock> netherrackNode(String name, int limit, TagKey<?>... tags) {
        return node(name, limit, Blocks.NETHERRACK, netherrackNodeProperties(),
                addTags(tags, Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK));
    }

    private static BlockEntry<OreNodeBlock> node(
            String name, int quantity, Block baseStone, BlockBehaviour.Properties properties, TagKey<?>... tags
    ) {
        return REGISTRATE.block(name, (prop) -> new OreNodeBlock(properties, baseStone.defaultBlockState()))
                         .transform(tagBlockOrItem(tags))
                         .tag(Tags.Items.ORES)
                         .build()
                         .tag(BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.ORES, ModTags.Blocks.ORE_NODES, AllTags.AllBlockTags.NON_BREAKABLE.tag)
                         .transform(TagGen.pickaxeOnly())
                         .blockstate(ModCommonBlockModelGen.defaultOverlay())
                         .loot((prov, block) -> prov.add(block, ModBlockLootTableGen.createOreNodeDrop(block)))
                         .transform(NodeYields.setNodeValue(quantity))
                         .onRegisterAfter(Registries.ITEM, item -> ItemDescription.useKey(item, "block.createreautomated.ore_node"))
                         .register();
    }

    private static BlockEntry<InfiniteNodeBlock> infiniteNode(String name, TagKey<?>... tags) {
        return REGISTRATE.block(name, InfiniteNodeBlock::new)
                         .initialProperties(() -> Blocks.STONE)
                         .properties(p -> p.noOcclusion().strength(30f).requiresCorrectToolForDrops())
                         .tag(BlockTags.NEEDS_IRON_TOOL)
                         .transform(TagGen.pickaxeOnly())
                         .blockstate((ctx, prov) ->
                                 prov.simpleBlock(
                                         ctx.get(),
                                         prov.models()
                                             .singleTexture(ctx.getName(), prov.modLoc("block/infinite_node_base"),
                                                     "node", ctx.getId().withPrefix("block/"))
                                             .renderType(prov.mcLoc("cutout_mipped"))
                                 )
                         )
                         .transform(tagBlockOrItem(tags))
                         .build()
                         .onRegisterAfter(Registries.ITEM, item -> ItemDescription.useKey(item, "block.createreautomated.infinite_node"))
                         .register();
    }

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockOrItem(TagKey<?>... tags) {
        return b -> {
            ItemBuilder<BlockItem, BlockBuilder<T, P>> item = b.item();
            for (TagKey<?> tag : tags) {
                tag.cast(Registries.ITEM).ifPresent(item::tag);
                tag.cast(Registries.BLOCK).ifPresent(b::tag);
            }
            return item;
        };
    }

    public static BlockBehaviour.Properties netherrackNodeProperties() {
        return stoneNodeProperties().sound(SoundType.NETHERRACK).strength(26f);
    }

    public static BlockBehaviour.Properties deepslateNodeProperties() {
        return stoneNodeProperties().sound(SoundType.DEEPSLATE).strength(30f);
    }

    public static BlockBehaviour.Properties stoneNodeProperties() {
        return BlockBehaviour.Properties.of()
                                        .requiresCorrectToolForDrops()
                                        .lightLevel(state -> 10 - state.getValue(OreNodeBlock.DEPLETION))
                                        .sound(SoundType.STONE)
                                        .pushReaction(PushReaction.BLOCK)
                                        .strength(28f);
    }

    public static TagKey<?>[] addTags(TagKey<?>[] original, @Nonnull TagKey<?>... toAdd) {
        TagKey<?>[] newTags = Arrays.copyOf(original, original.length + toAdd.length);
        System.arraycopy(toAdd, 0, newTags, original.length, toAdd.length);
        return newTags;
    }

    public static void register() {}
}
