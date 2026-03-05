package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> DRILL_ANY = createTag("drill_any");
        public static final TagKey<Item> DRILL_TIER_1 = createTag("drill_tier_1");
        public static final TagKey<Item> DRILL_TIER_2 = createTag("drill_tier_2");
        public static final TagKey<Item> DRILL_TIER_3 = createTag("drill_tier_3");

        @Nonnull
        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(CreateReAutomated.asResource(name));
        }
    }
}
