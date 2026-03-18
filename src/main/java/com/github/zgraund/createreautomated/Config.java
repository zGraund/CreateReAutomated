package com.github.zgraund.createreautomated;

import com.github.zgraund.createreautomated.api.OreNodeBlockIndex;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
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
        OreNodeBlockIndex.NODE_VALUES.registerProvider(server().nodeValues::getNodeValue);
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

    public static class Client extends ConfigBase {
        public final ConfigBool debugOreNodeOverlay = b(
                false,
                "debugOreNode",
                "Whether to display debug information when an Ore Node is right-clicked."
        );
        public final ConfigBool debugExtractorOverlay = b(
                false,
                "debugExtractor",
                "Whether to display debug information when looking at an extractor with the Engineer's Goggles equipped."
        );

        @Nonnull
        @Override
        public String getName() {
            return "client";
        }
    }

    public static class Common extends ConfigBase {
        public final ConfigBool worldgen =
                b(true, "worldGen", "Whether Create Re-Automated should generate nodes in the world.");

        @Nonnull
        @Override
        public String getName() {
            return "common";
        }
    }

    public static class Server extends ConfigBase {
        public final ConfigFloat extractorStress = f(
                64,
                0,
                "extractorStress",
                "[in Stress Units]",
                "How much stress impact does the extractor generate. Note that this cost is doubled for every speed increase it receives."
        );

        public final NodeValues nodeValues = nested(1, NodeValues::new, "Configure each node extraction limit.");

        public DoubleSupplier getExtractorImpact(Block block) {
            return block == ModBlocks.EXTRACTOR.get() ? extractorStress::get : null;
        }

        @Nonnull
        @Override
        public String getName() {
            return "server";
        }

        public static class NodeValues extends ConfigBase {
            private static final Object2IntMap<ResourceLocation> DEFAULT_VALUES = new Object2IntOpenHashMap<>();

            private final Map<ResourceLocation, ModConfigSpec.ConfigValue<Integer>> extractions = new HashMap<>();

            public static void setNodeValue(@Nonnull DeferredBlock<? extends Block> block, int value) {
                DEFAULT_VALUES.put(block.getId(), value);
            }

            @Override
            public void registerAll(@Nonnull ModConfigSpec.Builder builder) {
                builder.comment("Configure how many times each node can be extracted before turning into its base rock").push("extraction");
                DEFAULT_VALUES.forEach((id, value) ->
                        extractions.put(id, builder.define(id.getPath(), value))
                );
                builder.pop();
            }

            public IntSupplier getNodeValue(Block node) {
                ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(node);
                ModConfigSpec.ConfigValue<Integer> value = extractions.get(id);
                return id == null ? null : value::get;
            }

            @Nonnull
            @Override
            public String getName() {
                return "nodeValues";
            }
        }
    }
}
