package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModCreativeModeTab;
import com.github.zgraund.createreautomated.item.ModItems;
import com.github.zgraund.createreautomated.recipe.ModRecipes;
import com.github.zgraund.createreautomated.registry.ModDataComponents;
import com.github.zgraund.createreautomated.registry.ModFeatures;
import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.github.zgraund.createreautomated.registry.ModPlacementModifiers;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

@Mod(CreateReAutomated.MOD_ID)
public class CreateReAutomated {
    public static final String MOD_ID = "createreautomated";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateReAutomated(@Nonnull IEventBus modEventBus, @Nonnull ModContainer modContainer) {
        ModCreativeModeTab.register(modEventBus);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        ModBlockEntities.register(modEventBus);

        ModRecipes.register(modEventBus);

        ModDataComponents.register(modEventBus);

        ModFeatures.register(modEventBus);
        ModPlacementModifiers.register(modEventBus);

        ModPartialModels.init();

        Config.register(modContainer);
    }

    @Nonnull
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
