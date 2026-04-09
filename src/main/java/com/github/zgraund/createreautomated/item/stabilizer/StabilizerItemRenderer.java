package com.github.zgraund.createreautomated.item.stabilizer;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class StabilizerItemRenderer extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms,
                          MultiBufferSource buffer, int light, int overlay) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        LocalPlayer player = minecraft.player;

        boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        if (player == null || !player.isUsingItem() || player.getUseItemRemainingTicks() <= 0 || player.getUseItem() != stack) {
            itemRenderer.render(stack, ItemDisplayContext.NONE, leftHand, ms, buffer, light, overlay, model.getOriginalModel());
        } else {
            int maxTicks = stack.getItem().getUseDuration(stack, player);
            float percentage = (float) player.getUseItemRemainingTicks() / maxTicks;
            float maxOffset = 0.3125f;
            float offset = maxOffset - (percentage * maxOffset);

            for (float degrees : new float[]{0, 90, 180, 270}) {
                int modifier = leftHand ? -1 : 1;
                ms.pushPose();
                ms.mulPose(Axis.YP.rotationDegrees(modifier * degrees));
                ms.translate(-offset, 0, -offset);
                renderer.render(ModPartialModels.STABILIZER.get(), light);
                ms.popPose();
            }
        }
    }
}
