package com.github.zgraund.createreautomated.block.node;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class OreNodeRenderer implements BlockEntityRenderer<OreNodeEntity> {
    public OreNodeRenderer(BlockEntityRendererProvider.Context context) {}

    // TODO: need testing
    @Override
    public void render(@Nonnull OreNodeEntity blockEntity, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        if (blockEntity.getLevel() == null) return;
        int stage = blockEntity.getRemaining();
        if (stage < 0 || stage > 9) return;

        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();

        VertexConsumer consumer = new SheetedDecalTextureGenerator(
                bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(stage)), poseStack.last(), 1
        );

        Minecraft.getInstance().getBlockRenderer().renderBreakingTexture(state, pos, level, poseStack, consumer, level.getModelData(pos));
    }
}
