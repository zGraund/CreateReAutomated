package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateReAutomated.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModBlocks.EXTRACTOR.getId().toString(), modLoc("block/ore_extractor/item"));
        // TODO: add texture
        ModItems.getAllDrills()
                .forEach(drill ->
                        withExistingParent(drill.getId().toString(), mcLoc("item/generated"))
                                .texture("layer0", CreateReAutomated.asResource("item/drill_head"))
                );

        basicItem(ModItems.COPPER_BIT.get());
        basicItem(ModItems.IRON_BIT.get());
        basicItem(ModItems.GOLD_BIT.get());
        basicItem(ModItems.DIAMOND_BIT.get());
    }
}
