package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModCommonBlockModelGen extends BlockStateProvider {
    public ModCommonBlockModelGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CreateReAutomated.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        String destroyStage = "block/node_overlays/node_destroy_stage_";
        for (int i = 0; i <= 9; i++) {
            models().cubeAll(destroyStage + i, modLoc(destroyStage + i)).renderType(mcLoc("cutout"));
        }
    }
}
