package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.config.ModConfigScreen;
import com.github.zgraund.createreautomated.item.stabilizer.StabilizerItemRenderer;
import com.github.zgraund.createreautomated.ponder.ModPonderPlugin;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import javax.annotation.Nonnull;

@Mod(value = CreateReAutomated.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateReAutomated.MOD_ID, value = Dist.CLIENT)
public class CreateReAutomatedClient {
    public CreateReAutomatedClient(@Nonnull ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ModConfigScreen::new);
    }

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new ModPonderPlugin());
        setupConfigScreen();
    }

    private static void setupConfigScreen() {
        BaseConfigScreen.setDefaultActionFor(CreateReAutomated.MOD_ID, base -> base
                .withSpecs(Config.client().specification, Config.common().specification, Config.server().specification)
        );
    }

    @SubscribeEvent
    public static void registerClientExtensions(@Nonnull RegisterClientExtensionsEvent event) {
        event.registerItem(SimpleCustomRenderer.create(ModItems.STABILIZER.get(), new StabilizerItemRenderer()), ModItems.STABILIZER);
    }
}
