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
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class AnimatedExtractor extends AnimatedKinetics {
    protected BlockState top = ExtractorBlock.getTop();
    protected BlockState bottom = ExtractorBlock.getBottom();
    protected PartialModel cog = AllPartialModels.COGWHEEL;
    protected Block node = ModBlocks.DIAMOND_NODE.get();
    protected Item drill = ModItems.DIAMOND_DRILL.get();

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

        blockElement(cog)
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

    public AnimatedExtractor setNode(Block node) {
        this.node = node;
        return this;
    }

    public AnimatedExtractor setDrill(Item drill) {
        this.drill = drill;
        return this;
    }
}
