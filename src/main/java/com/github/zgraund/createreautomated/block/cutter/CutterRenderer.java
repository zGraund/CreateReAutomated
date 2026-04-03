package com.github.zgraund.createreautomated.block.cutter;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class CutterRenderer extends KineticBlockEntityRenderer<CutterBlockEntity> {
    public CutterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(@Nonnull CutterBlockEntity be, float partialTicks, PoseStack ms, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        // TODO: make visual

        BlockState state = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        BlockState shaft = shaft(getRotationAxisOf(be));
        renderRotatingBuffer(be, getRotatedModel(be, shaft), ms, vb, light);

//        if (be.isActive()) {
        SuperByteBuffer nozzle = CachedBuffers.partial(ModPartialModels.NOZZLE, state);
        Direction dir = state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING).getOpposite();
        nozzle.translate(be.getNozzleX(), be.getNozzleY(), be.getNozzleZ()).light(light).renderInto(ms, vb);
//        }
    }

    @Override
    protected BlockState getRenderedBlockState(CutterBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
