package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.worldgen.OreNodeFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class ModFeatures {
    private static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, CreateReAutomated.MOD_ID);

    public static final DeferredHolder<Feature<?>, OreNodeFeature> ORE_NODE_FEATURE =
            FEATURES.register("ore_node", OreNodeFeature::new);

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}
