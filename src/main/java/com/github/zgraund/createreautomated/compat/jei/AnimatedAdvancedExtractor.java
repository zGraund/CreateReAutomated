package com.github.zgraund.createreautomated.compat.jei;

import com.github.zgraund.createreautomated.block.advancedextractor.AdvancedExtractorBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class AnimatedAdvancedExtractor extends AnimatedExtractor {
    protected FluidStack fluid = FluidStack.EMPTY;

    public AnimatedAdvancedExtractor() {
        this.top = AdvancedExtractorBlock.getTop();
        this.bottom = AdvancedExtractorBlock.getBottom();
        this.cog = AllPartialModels.SHAFT;
    }

    @Override
    public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
        super.draw(graphics, xOffset, yOffset);

        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrixStack.scale(22, 22, 22);
        UIRenderHelper.flipForGuiRender(matrixStack);

        float minX = 1 / 16f;
        float maxX = 15 / 16f;
        float minY = 6 / 16f;
        float maxY = 10 / 16f;
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                fluid, minX, minY + 1f, minX, maxX, maxY + 1f, maxX, graphics.bufferSource(), matrixStack, LightTexture.FULL_BRIGHT, false, true
        );

        matrixStack.popPose();
    }

    public AnimatedAdvancedExtractor setFluid(FluidStack fluid) {
        this.fluid = fluid;
        return this;
    }
}
