package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CreateReAutomated.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(ModBlocks.ORE_NODE.get()).forAllStates(state -> {
            String modelName = state.getValue(OreNodeBlock.RESOURCES).getSerializedName() + (state.getValue(OreNodeBlock.NATURAL) ? "_natural" : "");
            ModelFile model = models().cubeAll("block/ore_node/" + modelName, modLoc("block/ore_node/" + modelName));
            return ConfiguredModel.builder().modelFile(model).build();
        });
        simpleBlockItem(ModBlocks.ORE_NODE.get(), models().getExistingFile(modLoc("block/ore_node/rich")));
    }
}
