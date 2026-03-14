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
        public static final TagKey<Block> ORE_NODES = common("ore_nodes");

        @Nonnull
        private static TagKey<Block> mod(String name) {
            return BlockTags.create(CreateReAutomated.asResource(name));
        }

        @Nonnull
        private static TagKey<Block> common(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> DRILLS = common("drills");
        public static final TagKey<Item> DRILL_TIER_1 = common("drills/tier_1");
        public static final TagKey<Item> DRILL_TIER_2 = common("drills/tier_2");
        public static final TagKey<Item> DRILL_TIER_3 = common("drills/tier_3");
        public static final TagKey<Item> AT_MOST_TIER_1 = common("drills/at_most_tier_1");
        public static final TagKey<Item> AT_MOST_TIER_2 = common("drills/at_most_tier_2");
        public static final TagKey<Item> AT_MOST_TIER_3 = common("drills/at_most_tier_3");
        public static final TagKey<Item> AT_LEAST_TIER_1 = common("drills/at_least_tier_1");
        public static final TagKey<Item> AT_LEAST_TIER_2 = common("drills/at_least_tier_2");
        public static final TagKey<Item> AT_LEAST_TIER_3 = common("drills/at_least_tier_3");

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
