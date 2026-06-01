package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.registry.ModDataComponents;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

public class ModBlockLootTableGen {
    @Nonnull
    public static LootTable.Builder createOreNodeDrop(Block block) {
        return LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .add(LootItem.lootTableItem(block)
                                                     .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                                                              .setProperties(
                                                                                                      StatePropertiesPredicate.Builder
                                                                                                              .properties()
                                                                                                              .hasProperty(OreNodeBlock.STABLE, true)
                                                                                              )
                                                     )
                                                     .apply(CopyBlockState.copyState(block).copy(OreNodeBlock.DEPLETION))
                                                     .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                                  .include(ModDataComponents.NODE_REMAINING_EXTRACTIONS.get())
                                                     )
                                        )
                        );
    }

    @Nonnull
    @Contract(pure = true)
    public static <T extends Block> NonNullBiConsumer<RegistrateBlockLootTables, T> createExtractorLootTable() {
        return (prov, block) ->
                prov.add(block, ModBlockLootTableGen.createSinglePropConditionTable(block, AbstractExtractorBlock.HALF, DoubleBlockHalf.LOWER));
    }

    @Nonnull
    public static <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(
            Block block, Property<T> property, T value
    ) {
        return LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(block)
                                                     .when(ExplosionCondition.survivesExplosion())
                                                     .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                                                              .setProperties(
                                                                                                      StatePropertiesPredicate.Builder
                                                                                                              .properties()
                                                                                                              .hasProperty(property, value)
                                                                                              )
                                                     )
                                        )
                        );

    }
}
