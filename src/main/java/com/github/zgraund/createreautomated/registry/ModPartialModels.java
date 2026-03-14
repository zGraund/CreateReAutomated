package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ModPartialModels {
    public static final Map<ResourceLocation, PartialModel> DRILL_PARTIAL_INDEX = new HashMap<>();

    public static final PartialModel HALF_COG = block("half_shaft_cogwheel");

    public static final PartialModel IRON_DRILL = drill("iron_drill");
    public static final PartialModel DIAMOND_DRILL = drill("diamond_drill");
    public static final PartialModel NETHERITE_DRILL = drill("netherite_drill");

    private static PartialModel block(String path) {
        return PartialModel.of(CreateReAutomated.asResource("block/" + path));
    }

    private static PartialModel drill(String path) {
        return drill(CreateReAutomated.asResource(path));
    }

    public static PartialModel drill(@Nonnull ResourceLocation path) {
        PartialModel model = PartialModel.of(path.withPrefix("block/"));
        DRILL_PARTIAL_INDEX.put(path, model);
        return model;
    }

    public static PartialModel getOrDefault(Item drill) {
        return DRILL_PARTIAL_INDEX.getOrDefault(BuiltInRegistries.ITEM.getKey(drill), IRON_DRILL);
    }

    public static void init() {}
}
