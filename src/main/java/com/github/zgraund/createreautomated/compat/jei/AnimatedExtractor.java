package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.api.DrillPartialIndex;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class AnimatedExtractor extends AnimatedKinetics {
    private final BlockState top = ExtractorBlock.getTop();
    private final BlockState bottom = ExtractorBlock.getBottom();
    private Block node = ModBlocks.DIAMOND_NODE.get();
    private Item drill = ModItems.DIAMOND_DRILL.get();

    public void draw(GuiGraphics graphics, int xOffset, int yOffset, Block node, Item drill) {
        this.node = node;
        this.drill = drill;
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

        BlockState state = node instanceof OreNodeBlock nodeBlock ? nodeBlock.unstable() : node.defaultBlockState();
        blockElement(state)
                .atLocal(0, 1, 0)
                .scale(scale)
                .render(graphics);

        blockElement(AllPartialModels.COGWHEEL)
                .atLocal(0, -1, 0)
                .rotateBlock(0, getCurrentAngle() * 2, 0)
                .scale(scale)
                .render(graphics);

        blockElement(DrillPartialIndex.getOrDefaultModel(drill))
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
