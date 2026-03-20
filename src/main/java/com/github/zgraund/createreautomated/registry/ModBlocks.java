package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.OreNodeBlockIndex;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.config.NodeValues;
import com.github.zgraund.createreautomated.datagen.ModBlockLootTableGen;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.Tags;

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
                      .item(DoubleHighBlockItem::new)
                      .model(AssetLookup.customBlockItemModel("_", "item"))
                      .build()
                      .register();

    public static final BlockEntry<OreNodeBlock>
            COPPER_NODE = stoneNode("copper_node", 100, Tags.Blocks.ORES_COPPER),
            IRON_NODE = stoneNode("iron_node", 200, Tags.Blocks.ORES_IRON),
            GOLD_NODE = stoneNode("gold_node", 50, Tags.Blocks.ORES_GOLD),
            DIAMOND_NODE = stoneNode("diamond_node", 20, Tags.Blocks.ORES_DIAMOND);

    public static final BlockEntry<OreNodeBlock>
            DEEPSLATE_COPPER_NODE = deepslateNode("deepslate_copper_node", 100, Tags.Blocks.ORES_COPPER),
            DEEPSLATE_IRON_NODE = deepslateNode("deepslate_iron_node", 250, Tags.Blocks.ORES_IRON),
            DEEPSLATE_GOLD_NODE = deepslateNode("deepslate_gold_node", 60, Tags.Blocks.ORES_GOLD),
            DEEPSLATE_DIAMOND_NODE = deepslateNode("deepslate_diamond_node", 20, Tags.Blocks.ORES_DIAMOND);

    public static final BlockEntry<OreNodeBlock>
            NETHER_GOLD_NODE = netherrackNode("nether_gold_node", 250, Tags.Blocks.ORES_GOLD);

    private static BlockEntry<OreNodeBlock> stoneNode(String name, int limit, TagKey<?> tags) {
        return node(name, limit, Blocks.COBBLESTONE, stoneNodeProperties(), tags);
    }

    private static BlockEntry<OreNodeBlock> deepslateNode(String name, int limit, TagKey<?>... tags) {
        return node(name, limit, Blocks.COBBLED_DEEPSLATE, deepslateNodeProperties(), tags);
    }

    private static BlockEntry<OreNodeBlock> netherrackNode(String name, int limit, TagKey<?>... tags) {
        return node(name, limit, Blocks.NETHERRACK, netherrackNodeProperties(), tags);
    }

    private static BlockEntry<OreNodeBlock> node(
            String name, int quantity, Block baseStone, BlockBehaviour.Properties properties, TagKey<?>... tags
    ) {
        return REGISTRATE.block(name, (prop) -> new OreNodeBlock(properties, baseStone.defaultBlockState()))
                         .transform(tagBlockOrItem(tags))
                         .build()
                         .tag(BlockTags.NEEDS_IRON_TOOL)
                         .tag(ModTags.Blocks.ORE_NODES)
                         .transform(TagGen.pickaxeOnly())
                         .blockstate((ctx, prov) -> {
                             // TODO: refactor this
                             ResourceLocation texture = ctx.getId().withPrefix("block/");
                             prov.models().cubeAll("block/" + ctx.getId().getPath(), texture);
                             String destroyStage = "block/node_overlays/node_destroy_stage_";
                             MultiPartBlockStateBuilder nodeState = prov.getMultipartBuilder(ctx.get())
                                                                        .part()
                                                                        .modelFile(prov.models().getExistingFile(texture))
                                                                        .addModel()
                                                                        .end();
                             for (int i = 1; i < OreNodeBlock.DEPLETION.getPossibleValues().size(); i++) {
                                 nodeState.part()
                                          .modelFile(prov.models().getExistingFile(prov.modLoc(destroyStage + (i - 1))))
                                          .addModel()
                                          .condition(OreNodeBlock.DEPLETION, i)
                                          .end();
                             }
                         })
                         .loot((prov, block) -> prov.add(block, ModBlockLootTableGen.createOreNodeDrop(block)))
                         .transform(NodeValues.setNodeValue(quantity))
                         .transform(OreNodeBlockIndex::register)
                         .onRegisterAfter(Registries.ITEM, item -> ItemDescription.useKey(item, "block.createreautomated.ore_node"))
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
        return stoneNodeProperties().sound(SoundType.NETHERRACK).strength(4f);
    }

    public static BlockBehaviour.Properties deepslateNodeProperties() {
        return stoneNodeProperties().sound(SoundType.DEEPSLATE).strength(6f);
    }

    public static BlockBehaviour.Properties stoneNodeProperties() {
        return BlockBehaviour.Properties.of()
                                        .requiresCorrectToolForDrops()
                                        .lightLevel(state -> 10 - state.getValue(OreNodeBlock.DEPLETION))
                                        .sound(SoundType.STONE)
                                        .pushReaction(PushReaction.BLOCK)
                                        .strength(5f);
    }

    public static void register() {}
}
