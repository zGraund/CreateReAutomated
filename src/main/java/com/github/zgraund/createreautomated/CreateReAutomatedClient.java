package com.github.zgraund.createreautomated;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import javax.annotation.Nonnull;

@Mod(value = CreateReAutomated.MOD_ID, dist = Dist.CLIENT)
//@EventBusSubscriber(modid = CreateReAutomated.MOD_ID, value = Dist.CLIENT)
public class CreateReAutomatedClient {
    public CreateReAutomatedClient(@Nonnull ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
