package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
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
            prov.models().cubeAll("block/" + ctx.getId().getPath(), texture);
            MultiPartBlockStateBuilder nodeState = prov.getMultipartBuilder(ctx.get())
                                                       .part()
                                                       .modelFile(prov.models().getExistingFile(texture))
                                                       .addModel()
                                                       .end();
            for (int i = 1; i < OreNodeBlock.DEPLETION.getPossibleValues().size(); i++) {
                nodeState.part()
                         .modelFile(prov.models().getExistingFile(prov.modLoc(DESTROY_STAGE_PATH + (i - 1))))
                         .addModel()
                         .condition(OreNodeBlock.DEPLETION, i)
                         .end();
            }
            nodeState.part()
                     .modelFile(prov.models().cubeColumn(
                             OVERLAY_PATH + "stabilizer",
                             prov.modLoc(OVERLAY_PATH + "stabilizer_side"),
                             prov.modLoc(OVERLAY_PATH + "stabilizer_top")
                     ).renderType(prov.mcLoc(RenderType.cutoutMipped().name)))
                     .addModel()
                     .condition(OreNodeBlock.STABLE, true)
                     .end();
        };
    }

    @Override
    protected void registerStatesAndModels() {
        for (int i = 0; i <= 9; i++) {
            models().cubeAll(DESTROY_STAGE_PATH + i, modLoc(DESTROY_STAGE_PATH + i)).renderType(mcLoc("cutout"));
        }
    }
}
