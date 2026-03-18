package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nonnull;

public class ModBlockStateProvider extends BlockStateProvider {
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

        String destroyStage = "block/node_overlays/node_destroy_stage_";
        for (int i = 0; i <= 9; i++) {
            models().cubeAll(destroyStage + i, modLoc(destroyStage + i)).renderType(mcLoc("cutout"));
        }
        defaultOreNodeWithOverlay(ModBlocks.COPPER_NODE);
        defaultOreNodeWithOverlay(ModBlocks.IRON_NODE);
        defaultOreNodeWithOverlay(ModBlocks.GOLD_NODE);
        defaultOreNodeWithOverlay(ModBlocks.DIAMOND_NODE);
        defaultOreNodeWithOverlay(ModBlocks.DEEPSLATE_COPPER_NODE);
        defaultOreNodeWithOverlay(ModBlocks.DEEPSLATE_IRON_NODE);
        defaultOreNodeWithOverlay(ModBlocks.DEEPSLATE_GOLD_NODE);
        defaultOreNodeWithOverlay(ModBlocks.DEEPSLATE_DIAMOND_NODE);
        defaultOreNodeWithOverlay(ModBlocks.NETHER_GOLD_NODE);

        String drillPath = "block/drills/";
        ModItems.getAllDrills().forEach(drill -> {
            ResourceLocation id = drill.getId();
            models().withExistingParent(id.withPrefix(drillPath).toString(), modLoc("block/ore_extractor/drill")).texture("0", id.withPrefix(drillPath));
        });
    }

    public void defaultOreNodeWithOverlay(@Nonnull DeferredBlock<? extends Block> block) {
        ResourceLocation texture = block.getId().withPrefix("block/nodes/");
        models().cubeAll("block/nodes/" + block.getId().getPath(), texture);
        defaultOreNodeWithOverlay(block, texture);
    }

    public void defaultOreNodeWithOverlay(@Nonnull DeferredBlock<? extends Block> block, ResourceLocation baseTexture) {
        String destroyStage = "block/node_overlays/node_destroy_stage_";
        MultiPartBlockStateBuilder nodeState = getMultipartBuilder(block.get())
                .part()
                .modelFile(models().getExistingFile(baseTexture))
                .addModel()
                .end();
        for (int i = 1; i < OreNodeBlock.DEPLETION.getPossibleValues().size(); i++) {
            nodeState.part()
                     .modelFile(models().getExistingFile(modLoc(destroyStage + (i - 1))))
                     .addModel()
                     .condition(OreNodeBlock.DEPLETION, i)
                     .end();
        }
        simpleBlockItem(block.get(), models().getExistingFile(baseTexture));
    }
}
