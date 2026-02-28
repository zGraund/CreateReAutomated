package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class ModEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ExtractorBlockEntity.registerCapabilities(event);
    }
}
