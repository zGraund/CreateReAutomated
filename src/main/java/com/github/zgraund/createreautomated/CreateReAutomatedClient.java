package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.block.extractor.ExtractorRenderer;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.ponder.ModPonderPlugin;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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

        // Register nodes tooltip keys after the items have been registered
        ModBlocks.getAllNodes().forEach(nodeBlock ->
                ItemDescription.useKey(nodeBlock.block(), "block.createreautomated.ore_node")
        );
        // And same with drills tooltip
        ModItems.getAllDrills().forEach(drill ->
                ItemDescription.useKey(drill, "item.createreautomated.drill_head")
        );

        setupConfigScreen();
    }

    private static void setupConfigScreen() {
        BaseConfigScreen.setDefaultActionFor(CreateReAutomated.MOD_ID, base -> base
                .withSpecs(Config.client().specification, Config.common().specification, Config.server().specification)
        );
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
