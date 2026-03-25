package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateReAutomated.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NODE_REMAINING_EXTRACTIONS =
            DATA_COMPONENTS.registerComponentType(
                    "remaining",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
            );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
