package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

public class ExtractorRenderer extends KineticBlockEntityRenderer<ExtractorBlockEntity> {
    public ExtractorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Nonnull
    @Override
    public AABB getRenderBoundingBox(@Nonnull ExtractorBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    @Override
    protected void renderSafe(@Nonnull ExtractorBlockEntity be, float partialTicks, PoseStack ms, @Nonnull MultiBufferSource buffer, int light, int overlay) {
//        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
//        if (VisualizationManager.supportsVisualization(be.getLevel())) return;

        BlockState blockState = be.getBlockState();

        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        SuperByteBuffer cog = CachedBuffers.partial(be.hasDrill() ? ModPartialModels.HALF_COG : AllPartialModels.COGWHEEL, blockState);
        standardKineticRotationTransform(cog, be, light).renderInto(ms, vb);

        if (be.hasDrill()) {
            SuperByteBuffer drill = CachedBuffers.partial(ModPartialModels.DRILL, blockState);
            standardKineticRotationTransform(drill, be, light).translate(0, -0.8, 0).renderInto(ms, vb);
        }
    }
}
