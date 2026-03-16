package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class ModPartialModels {
    public static final PartialModel HALF_COG = block("half_shaft_cogwheel");

    public static final PartialModel IRON_DRILL = drill("iron_drill");
    public static final PartialModel DIAMOND_DRILL = drill("diamond_drill");
    public static final PartialModel NETHERITE_DRILL = drill("netherite_drill");

    private static PartialModel block(String path) {
        return PartialModel.of(CreateReAutomated.asResource("block/" + path));
    }

    private static PartialModel drill(String path) {
        return PartialModel.of(CreateReAutomated.asResource(path).withPrefix("block/drills/"));
    }

    public static void init() {}
}
