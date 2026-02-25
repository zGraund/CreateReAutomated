package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import javax.annotation.Nonnull;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Nonnull
    public static LootTable.Builder createOreNodeDrop(Block block) {
        return LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                                                 .setProperties(
                                                                                         StatePropertiesPredicate.Builder
                                                                                                 .properties()
                                                                                                 .hasProperty(OreNodeBlock.NATURAL, false)
                                                                                 )
                                        )
                                        .setRolls(ConstantValue.exactly(1.0f))
                                        .add(LootItem.lootTableItem(block).apply(CopyBlockState.copyState(block).copy(OreNodeBlock.RESOURCES)))
                        );
    }

    @Override
    protected void generate() {
        add(ModBlocks.EXTRACTOR.get(), block -> createSinglePropConditionTable(block, ExtractorBlock.HALF, DoubleBlockHalf.LOWER));
        add(ModBlocks.ORE_NODE.get(), ModBlockLootTableProvider::createOreNodeDrop);
        add(ModBlocks.ORE_NODE_LIMITED.get(), ModBlockLootTableProvider::createOreNodeDrop);
    }

    @Nonnull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
