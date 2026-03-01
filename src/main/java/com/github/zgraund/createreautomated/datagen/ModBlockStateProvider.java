package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import net.minecraft.core.registries.BuiltInRegistries;
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
        generateOreNodeModels(ModBlocks.ORE_NODE.get());
        generateOreNodeModels(ModBlocks.ORE_NODE_LIMITED.get());
        // FIXME: change this model location
//        simpleBlockItem(ModBlocks.ORE_NODE.get(), models().getExistingFile(modLoc("block/ore_node/ore_node_rich")));
//        simpleBlockItem(ModBlocks.ORE_NODE_LIMITED.get(), models().getExistingFile(modLoc("block/ore_node/ore_node_limited_rich")));
    }

    public void generateOreNodeModels(OreNodeBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String modelName = state.getValue(OreNodeBlock.RESOURCES).getSerializedName() + (state.getValue(OreNodeBlock.NATURAL) ? "_natural" : "");
            ModelFile model = models().cubeAll(
                    "block/ore_node/" + BuiltInRegistries.BLOCK.getKey(block).getPath() + "_" + modelName,
//                    modLoc("block/ore_node/" + modelName)
                    mcLoc("block/diamond_ore")
            );
            return ConfiguredModel.builder().modelFile(model).build();
        });
    }
}
