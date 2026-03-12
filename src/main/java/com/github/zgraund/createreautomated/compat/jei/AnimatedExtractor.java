package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class AnimatedExtractor extends AnimatedKinetics {
    private final BlockState top = ExtractorBlock.getTop();
    private final BlockState bottom = ExtractorBlock.getBottom();
    private Block node = ModBlocks.DIAMOND_NODE.get();

    public void draw(GuiGraphics graphics, int xOffset, int yOffset, Block node) {
        this.node = node;
        draw(graphics, xOffset, yOffset);
    }

    @Override
    public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();

        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        int scale = 22;

        blockElement(node.defaultBlockState())
                .atLocal(0, 1, 0)
                .scale(scale)
                .render(graphics);

        blockElement(AllPartialModels.COGWHEEL)
                .atLocal(0, -1, 0)
                .rotateBlock(0, getCurrentAngle() * 2, 0)
                .scale(scale)
                .render(graphics);

        blockElement(ModPartialModels.DRILL)
                .atLocal(0, -0.15, 0)
                .rotateBlock(0, getCurrentAngle() * 2, 0)
                .scale(scale)
                .render(graphics);

        blockElement(bottom)
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);
        blockElement(top)
                .atLocal(0, -1, 0)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }
}
