package com.github.zgraund.createreautomated.block.advancedextractor;

import com.github.zgraund.createreautomated.block.extractor.ExtractorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AdvancedExtractorRenderer extends ExtractorRenderer<AdvancedExtractorBlockEntity> {
    public AdvancedExtractorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(AdvancedExtractorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        float minX = 1 / 16f;
        float maxX = 15 / 16f;
        float minY = 6 / 16f;
        float maxY = 10 / 16f;
        float fluidLevel = minY + (be.getFillPercentage() * (maxY - minY));

        if (!be.isEmpty()) {
            ms.pushPose();
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                    be.getFluidStack(), minX, minY, minX, maxX, fluidLevel, maxX, buffer, ms, light, false, true
            );
            ms.popPose();
        }
    }

    @Override
    protected PartialModel getInnerModel(AdvancedExtractorBlockEntity be) {
        return AllPartialModels.SHAFT;
    }
}
