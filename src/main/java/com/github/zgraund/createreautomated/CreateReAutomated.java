package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.recipe.ModRecipes;
import com.github.zgraund.createreautomated.registry.*;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
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
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
                                                                      .defaultCreativeTab(ModCreativeModeTab.BASE.getKey())
                                                                      .setTooltipModifierFactory(item ->
                                                                              new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                                                                                      .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
                                                                      );

    public CreateReAutomated(@Nonnull IEventBus modEventBus, @Nonnull ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        ModCreativeModeTab.register(modEventBus);

        ModBlocks.register();
        ModItems.register();

        ModBlockEntities.register();

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
