package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateReAutomated.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModBlocks.EXTRACTOR.getId().toString(), modLoc("block/ore_extractor/item"));

        ModItems.getAllDrills().stream().map(DeferredItem::get).forEach(this::basicItem);

        basicItem(ModItems.COPPER_BIT.get());
        basicItem(ModItems.IRON_BIT.get());
        basicItem(ModItems.GOLD_BIT.get());
        basicItem(ModItems.DIAMOND_BIT.get());
    }
}
