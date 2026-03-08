package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.worldgen.ConfigPlacementFilter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class ModPlacementModifiers {
    private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, CreateReAutomated.MOD_ID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<ConfigPlacementFilter>> CONFIG_FILTER =
            PLACEMENT_MODIFIER_TYPES.register("config_filter", () -> () -> ConfigPlacementFilter.CODEC);

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        PLACEMENT_MODIFIER_TYPES.register(modEventBus);
    }
}
