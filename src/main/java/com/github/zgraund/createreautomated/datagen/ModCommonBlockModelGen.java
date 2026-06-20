package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

public class ModCommonBlockModelGen extends BlockStateProvider {
    public static final String OVERLAY_PATH = "block/node_overlays/";
    public static final String DESTROY_STAGE_PATH = OVERLAY_PATH + "node_destroy_stage_";

    public ModCommonBlockModelGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CreateReAutomated.MOD_ID, exFileHelper);
    }

    @Nonnull
    @Contract(pure = true)
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> defaultOverlay() {
        return (ctx, prov) -> {
            ResourceLocation texture = ctx.getId().withPrefix("block/");
            ModelFile model = prov.models().cubeAll("block/" + ctx.getId().getPath(), texture);
            MultiPartBlockStateBuilder nodeState = prov.getMultipartBuilder(ctx.get())
                                                       .part()
                                                       .modelFile(model)
                                                       .addModel()
                                                       .end();
            addOverlay(nodeState, prov, DESTROY_STAGE_PATH);
            addStabilizer(nodeState, prov, OVERLAY_PATH);
        };
    }

    @Nonnull
    @Contract(pure = true)
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> topSideNodeWithOverlay() {
        return (ctx, prov) -> {
            ResourceLocation texture = ctx.getId().withPrefix("block/");
            ModelFile model = prov.models().cubeColumn(
                    "block/" + ctx.getId().getPath(),
                    texture.withSuffix("_side"),
                    texture.withSuffix("_top")
            );
            MultiPartBlockStateBuilder nodeState = prov.getMultipartBuilder(ctx.get())
                                                       .part()
                                                       .modelFile(model)
                                                       .addModel()
                                                       .end();
            addOverlay(nodeState, prov, DESTROY_STAGE_PATH);
            addStabilizer(nodeState, prov, OVERLAY_PATH);
        };
    }

    public static void addOverlay(MultiPartBlockStateBuilder model, RegistrateBlockstateProvider prov, String overlayName) {
        for (int i = 1; i < OreNodeBlock.DEPLETION.getPossibleValues().size(); i++) {
            model.part()
                 .modelFile(prov.models().getExistingFile(prov.modLoc(overlayName + (i - 1))))
                 .addModel()
                 .condition(OreNodeBlock.DEPLETION, i)
                 .end();
        }
    }

    public static void addStabilizer(@Nonnull MultiPartBlockStateBuilder model, @Nonnull RegistrateBlockstateProvider prov, String path) {
        model.part()
             .modelFile(prov.models().cubeColumn(
                     path + "stabilizer",
                     prov.modLoc(path + "stabilizer_side"),
                     prov.modLoc(path + "stabilizer_top")
             ).renderType(prov.mcLoc(RenderType.cutoutMipped().name)))
             .addModel()
             .condition(OreNodeBlock.STABLE, true)
             .end();

    }

    @Nonnull
    @Contract(pure = true)
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> extractorModel() {
        return (ctx, prov) ->
                BlockStateGen.simpleBlock(ctx, prov, state ->
                        prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/" + state.getValue(AbstractExtractorBlock.HALF)))
                );
    }

    @Override
    protected void registerStatesAndModels() {
        for (int i = 0; i <= 9; i++) {
            models().cubeAll(DESTROY_STAGE_PATH + i, modLoc(DESTROY_STAGE_PATH + i)).renderType(mcLoc("cutout"));
        }
    }
}
