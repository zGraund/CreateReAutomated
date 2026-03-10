package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(@Nonnull HolderLookup.Provider provider) {
        tag(ModTags.Items.DRILL_ANY).add(ModItems.getAllDrills().stream().map(DeferredItem::get).toArray(Item[]::new));

        tag(ModTags.Items.DRILL_TIER_1)
                .add(ModItems.IRON_DRILL.get())
                .addTags(
                        ModTags.Items.DRILL_TIER_2,
                        ModTags.Items.DRILL_TIER_3
                );
        tag(ModTags.Items.DRILL_TIER_2)
                .add(ModItems.DIAMOND_DRILL.get())
                .addTags(ModTags.Items.DRILL_TIER_3);
        tag(ModTags.Items.DRILL_TIER_3)
                .add(ModItems.NETHERITE_DRILL.get());
    }
}
