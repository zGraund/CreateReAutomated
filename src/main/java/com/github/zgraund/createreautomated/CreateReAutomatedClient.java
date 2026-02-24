package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.block.ExtractorRenderer;
import com.github.zgraund.createreautomated.block.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import javax.annotation.Nonnull;

@Mod(value = CreateReAutomated.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateReAutomated.MOD_ID, value = Dist.CLIENT)
public class CreateReAutomatedClient {
    public CreateReAutomatedClient(@Nonnull ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerBER(@Nonnull EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.EXTRACTOR_BE.get(), ExtractorRenderer::new);

        // TODO: create visual
//        SimpleBlockEntityVisualizer.builder(ModBlockEntities.EXTRACTOR_BE.get())
//                                   .factory(SingleAxisRotatingVisual.of(AllPartialModels.COGWHEEL))
//                                   .factory(SingleAxisRotatingVisual.of(PartialModel.of(CreateReAutomated.id("block/ore_extractor/drill"))))
//                                   .skipVanillaRender(be -> true)
//                                   .apply();
    }
}
