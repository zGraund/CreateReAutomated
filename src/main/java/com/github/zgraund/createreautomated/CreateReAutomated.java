package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.github.zgraund.createreautomated.item.ModCreativeModeTab;
import com.github.zgraund.createreautomated.item.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
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

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
