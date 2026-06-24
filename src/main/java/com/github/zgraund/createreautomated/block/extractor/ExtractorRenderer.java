package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class ExtractorRenderer<T extends ExtractorBlockEntity> extends KineticBlockEntityRenderer<T> {
    public ExtractorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(@Nonnull T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel())) return;

        BlockState blockState = be.getBlockState();

        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        SuperByteBuffer cog = CachedBuffers.partial(getInnerModel(be), blockState);
        standardKineticRotationTransform(cog, be, light).renderInto(ms, vb);

        if (be.hasDrill()) {
            float drillOffset = be.getDrillOffset(partialTicks);
            SuperByteBuffer drill = CachedBuffers.partial(be.getDrillModel(), blockState);
            // TODO: fix drill render when offset > 1
            standardKineticRotationTransform(drill, be, light).translate(new Vec3(0, -drillOffset, 0)).renderInto(ms, vb);
        }
    }

    protected PartialModel getInnerModel(@Nonnull T be) {
        return be.hasDrill() ? ModPartialModels.HALF_COG : AllPartialModels.COGWHEEL;
    }
}
