package com.github.zgraund.createreautomated.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;

public class NodeValues extends ConfigBase {
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
