package com.github.zgraund.createreautomated.block.liquidcooledextractor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LCExtractorRenderer extends KineticBlockEntityRenderer<LCExtractorBlockEntity> {
    public LCExtractorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(LCExtractorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        // The drill render is handled by the visual if supported
        if (!VisualizationManager.supportsVisualization(be.getLevel())) {
            BlockState blockState = be.getBlockState();

            VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

            SuperByteBuffer shaft = CachedBuffers.partial(AllPartialModels.SHAFT, blockState);
            standardKineticRotationTransform(shaft, be, light).renderInto(ms, vb);

            if (be.hasDrill()) {
                float drillOffset = be.getDrillOffset();
                SuperByteBuffer drill = CachedBuffers.partial(be.getDrillModel(), blockState);
                standardKineticRotationTransform(drill, be, light).translate(0, -drillOffset, 0).renderInto(ms, vb);
            }
        }

        float minX = 1 / 16f;
        float maxX = 15 / 16f;
        float minY = 5 / 16f;
        float maxY = 10 / 16f;
        float fluidLevel = minY + (be.getFillPercentage() * (maxY - minY));

        if (!be.tank.isEmpty()) {
            ms.pushPose();
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                    be.tank.getFluid(), minX, minY, minX, maxX, fluidLevel, maxX, buffer, ms, light, false, true
            );
            ms.popPose();
        }
    }
}
