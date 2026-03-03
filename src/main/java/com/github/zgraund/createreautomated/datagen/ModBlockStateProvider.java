package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {
    public static final OreNodeBlock.DepletionLevel[] ALL_DEPLETION_LEVELS = OreNodeBlock.DepletionLevel.values();

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CreateReAutomated.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(ModBlocks.EXTRACTOR.get()).forAllStates(blockState ->
                ConfiguredModel.builder()
                               .modelFile(models().getExistingFile(modLoc("block/ore_extractor/" + blockState.getValue(ExtractorBlock.HALF))))
                               .build()
        );

        String destroyStage = "block/ore_node/node_destroy_stage_";
        for (int i = 0; i <= 9; i++) {
            models().cubeAll(destroyStage + i, modLoc(destroyStage + i)).renderType(mcLoc("cutout"));
        }
        defaultOreNodeWithOverlay(ModBlocks.ORE_NODE, "block/diamond_ore");
        defaultOreNodeWithOverlay(ModBlocks.ORE_NODE_LIMITED, "block/emerald_ore");
        defaultOreNodeWithOverlay(ModBlocks.ORE_NODE_TEST, "block/gold_ore");
        simpleBlockItem(ModBlocks.ORE_NODE.get(), models().getExistingFile(mcLoc("block/diamond_ore")));
        simpleBlockItem(ModBlocks.ORE_NODE_LIMITED.get(), models().getExistingFile(mcLoc("block/emerald_ore")));
        simpleBlockItem(ModBlocks.ORE_NODE_TEST.get(), models().getExistingFile(mcLoc("block/gold_ore")));
    }

    public void defaultOreNodeWithOverlay(@Nonnull Supplier<? extends Block> block, String baseTexture) {
        String destroyStage = "block/ore_node/node_destroy_stage_";
        MultiPartBlockStateBuilder nodeState = getMultipartBuilder(block.get())
                .part()
                .modelFile(models().getExistingFile(mcLoc(baseTexture)))
                .addModel()
                .end();
        for (int i = 1; i < ALL_DEPLETION_LEVELS.length; i++) {
            nodeState.part()
                     .modelFile(models().getExistingFile(modLoc(destroyStage + (i - 1))))
                     .addModel()
                     .condition(OreNodeBlock.DEPLETION, ALL_DEPLETION_LEVELS[i])
                     .end();
        }
    }
}
