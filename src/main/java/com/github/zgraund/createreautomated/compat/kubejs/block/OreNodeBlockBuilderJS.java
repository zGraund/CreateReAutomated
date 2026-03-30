package com.github.zgraund.createreautomated.compat.kubejs.block;

import com.github.zgraund.createreautomated.api.OreNodeBlockIndex;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.compat.kubejs.CRAKubeJSPlugin;
import com.github.zgraund.createreautomated.datagen.ModBlockLootTableGen;
import com.simibubi.create.foundation.item.ItemDescription;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockItemBuilder;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class OreNodeBlockBuilderJS extends BlockBuilder {
    public boolean commonLoot = false;
    public int yield = 0;
    public BlockState baseStone = Blocks.AIR.defaultBlockState();

    public OreNodeBlockBuilderJS(ResourceLocation id) {
        super(id);
        itemBuilder = new BlockItemBuilder(id) {
            @Override
            public Item createObject() {
                // All this just to have the in game tooltip
                Item item = super.createObject();
                CRAKubeJSPlugin.registerItemTooltipModifier(item);
                ItemDescription.useKey(item, "block.createreautomated.ore_node");
                return item;
            }
        };
        ((BlockItemBuilder) itemBuilder).blockBuilder = this;
    }

    public OreNodeBlockBuilderJS baseStone(@Nonnull Block block) {
        this.baseStone = block.defaultBlockState();
        return this;
    }

    public OreNodeBlockBuilderJS baseStone(BlockState state) {
        this.baseStone = state;
        return this;
    }

    public OreNodeBlockBuilderJS yield(int yield) {
        this.yield = yield;
        return this;
    }

    public OreNodeBlockBuilderJS withCommonLoot() {
        this.commonLoot = true;
        return this;
    }

    @Override
    public @Nullable LootTable generateLootTable(KubeDataGenerator generator) {
        if (commonLoot)
            return ModBlockLootTableGen.createOreNodeDrop(get()).build();
        return super.generateLootTable(generator);
    }

    @Override
    public Block createObject() {
        Block block = new OreNodeBlock(createProperties(), baseStone);
        OreNodeBlockIndex.NODE_YIELDS.register(block, () -> yield);
        CRAKubeJSPlugin.VALID_BLOCKS_FOR_BE.add(block);
        return block;
    }
}
