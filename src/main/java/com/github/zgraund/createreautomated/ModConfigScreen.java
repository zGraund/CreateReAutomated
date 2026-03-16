package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModConfigScreen extends BaseConfigScreen {
    public static DelegatedStencilElement customShadow = new DelegatedStencilElement(
            (graphics, x, y, alpha) -> renderExtractor(graphics),
            (graphics, x, y, alpha) -> graphics.fill(-200, -200, 200, 200, 0x60_000000)
    );

    public ModConfigScreen(@Nullable Screen parent, String modID) {
        super(parent, modID);
    }

    protected static void renderExtractor(@Nonnull GuiGraphics graphics) {
        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        poseStack.translate(-100, 120, -200);
        poseStack.scale(200, 200, 1);

        GuiGameElement.of(ModPartialModels.IRON_DRILL)
                      .rotateBlock(165.5, cogSpin.getValue(partialTicks), 120)
                      .render(graphics);

        poseStack.popPose();
    }

    @Override
    protected void renderWindowBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft != null && this.minecraft.level != null) {
            //in game
            graphics.fill(0, 0, this.width, this.height, 0xb0_282c34);
        } else {
            //in menus
            renderMenuBackground(graphics, partialTicks);
        }
        customShadow.at(width * 0.5f, height * 0.5f, 0)
                    .render(graphics);
        renderBackground(graphics, mouseX, mouseY, partialTicks);
    }
}
