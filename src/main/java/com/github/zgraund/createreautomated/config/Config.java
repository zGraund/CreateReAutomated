package com.github.zgraund.createreautomated.config;

import com.github.zgraund.createreautomated.api.OreNodeBlockIndex;
import com.simibubi.create.api.stress.BlockStressValues;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class Config {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static Client client;
    private static Common common;
    private static Server server;

    public static Client client() {
        return client;
    }

    public static Common common() {
        return common;
    }

    public static Server server() {
        return server;
    }

    @Nonnull
    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register(ModContainer container) {
        client = register(Client::new, ModConfig.Type.CLIENT);
        common = register(Common::new, ModConfig.Type.COMMON);
        server = register(Server::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            container.registerConfig(pair.getKey(), pair.getValue().specification);

        BlockStressValues.IMPACTS.registerProvider(server()::getExtractorImpact);
        BlockStressValues.IMPACTS.registerProvider(server()::getCutterImpact);
        OreNodeBlockIndex.NODE_YIELDS.registerProvider(server().nodeYields::getNodeValue);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig().getSpec())
                config.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig().getSpec())
                config.onReload();
    }
}
