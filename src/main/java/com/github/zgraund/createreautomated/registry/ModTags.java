package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> ORE_NODE = createTag("ore_node");

        @Nonnull
        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(CreateReAutomated.asResource(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ANY_DRILL = createTag("any_drill");
        public static final TagKey<Item> DRILL_TIER_1 = createTag("drill_tier_1");
        public static final TagKey<Item> DRILL_TIER_2 = createTag("drill_tier_2");
        public static final TagKey<Item> DRILL_TIER_3 = createTag("drill_tier_3");
        public static final TagKey<Item> AT_MOST_TIER_1 = createTag("at_most_tier_1");
        public static final TagKey<Item> AT_MOST_TIER_2 = createTag("at_most_tier_2");
        public static final TagKey<Item> AT_MOST_TIER_3 = createTag("at_most_tier_3");
        public static final TagKey<Item> AT_LEAST_TIER_1 = createTag("at_least_tier_1");
        public static final TagKey<Item> AT_LEAST_TIER_2 = createTag("at_least_tier_2");
        public static final TagKey<Item> AT_LEAST_TIER_3 = createTag("at_least_tier_3");

        @Nonnull
        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(CreateReAutomated.asResource(name));
        }
    }
}
