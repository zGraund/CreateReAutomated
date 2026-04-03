package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class ModTags {
    public static class Blocks {
        public static final TagKey<Block>
                ORE_NODES = mod("ore_nodes"),
                COPPER_NODES = ores("copper_nodes"),
                IRON_NODES = ores("iron_nodes"),
                GOLD_NODES = ores("gold_nodes"),
                DIAMOND_NODES = ores("diamond_nodes"),
                NON_BREAKABLE = ores("non_breakable"),
                NON_HARVESTABLE = ores("non_harvestable"),
                NON_FRACTURABLE = ores("non_fracturable");

        @Nonnull
        private static TagKey<Block> mod(String name) {
            return BlockTags.create(CreateReAutomated.asResource(name));
        }

        @Nonnull
        private static TagKey<Block> ores(String name) {
            return mod("ore_nodes/" + name);
        }

        @Nonnull
        private static TagKey<Block> common(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ORE_NODES = mod("ore_nodes");
        // TODO: not sure about the groups
        public static final TagKey<Item>
                DRILLS = mod("drills"),
                DRILL_TIER_1 = mod("drills/tier_1"),
                DRILL_TIER_2 = mod("drills/tier_2"),
                DRILL_TIER_3 = mod("drills/tier_3"),
                AT_MOST_TIER_1 = mod("drills/at_most_tier_1"),
                AT_MOST_TIER_2 = mod("drills/at_most_tier_2"),
                AT_MOST_TIER_3 = mod("drills/at_most_tier_3"),
                AT_LEAST_TIER_1 = mod("drills/at_least_tier_1"),
                AT_LEAST_TIER_2 = mod("drills/at_least_tier_2"),
                AT_LEAST_TIER_3 = mod("drills/at_least_tier_3");

        @Nonnull
        private static TagKey<Item> mod(String name) {
            return ItemTags.create(CreateReAutomated.asResource(name));
        }

        @Nonnull
        private static TagKey<Item> common(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
