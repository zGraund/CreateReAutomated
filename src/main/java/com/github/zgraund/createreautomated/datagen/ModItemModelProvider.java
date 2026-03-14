package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nonnull;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateReAutomated.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModBlocks.EXTRACTOR.getId().toString(), modLoc("block/ore_extractor/item"));

        ModItems.getAllDrills().forEach(this::drill);

        bit(ModItems.COPPER_BIT);
        bit(ModItems.IRON_BIT);
        bit(ModItems.GOLD_BIT);
        bit(ModItems.DIAMOND_BIT);
    }

    private void drill(DeferredItem<Item> drill) {
        basicWithTexture(drill, "drills");
    }

    private void bit(DeferredItem<Item> bit) {
        basicWithTexture(bit, "bits");
    }

    private void basicWithTexture(@Nonnull DeferredItem<Item> item, String texture) {
        withExistingParent(item.getId().getPath(), "item/generated").texture("layer0", "item/" + texture + "/" + item.getId().getPath());
    }
}
