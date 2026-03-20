package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.registry.ModTags;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;

import javax.annotation.Nonnull;

public class ModTagGen {
    @SuppressWarnings("unchecked")
    public static void items(@Nonnull RegistrateItemTagsProvider provider) {
        provider.copy(ModTags.Blocks.ORE_NODES, ModTags.Items.ORE_NODES);

        provider.addTag(ModTags.Items.DRILLS)
                .addTags(
                        ModTags.Items.DRILL_TIER_1,
                        ModTags.Items.DRILL_TIER_2,
                        ModTags.Items.DRILL_TIER_3
                );

        provider.addTag(ModTags.Items.AT_MOST_TIER_1)
                .addTags(ModTags.Items.DRILL_TIER_1);
        provider.addTag(ModTags.Items.AT_MOST_TIER_2)
                .addTags(
                        ModTags.Items.DRILL_TIER_1,
                        ModTags.Items.DRILL_TIER_2
                );
        provider.addTag(ModTags.Items.AT_MOST_TIER_3)
                .addTags(
                        ModTags.Items.DRILL_TIER_1,
                        ModTags.Items.DRILL_TIER_2,
                        ModTags.Items.DRILL_TIER_3
                );

        provider.addTag(ModTags.Items.AT_LEAST_TIER_1)
                .addTags(
                        ModTags.Items.DRILL_TIER_1,
                        ModTags.Items.DRILL_TIER_2,
                        ModTags.Items.DRILL_TIER_3
                );
        provider.addTag(ModTags.Items.AT_LEAST_TIER_2)
                .addTags(
                        ModTags.Items.DRILL_TIER_2,
                        ModTags.Items.DRILL_TIER_3
                );
        provider.addTag(ModTags.Items.AT_LEAST_TIER_3)
                .addTags(ModTags.Items.DRILL_TIER_3);
    }
}
