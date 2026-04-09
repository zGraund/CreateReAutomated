package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class ModPartialModels {
    public static final String PATH = "partial/";

    public static final PartialModel HALF_COG = create("half_shaft_cogwheel");

    public static final PartialModel STABILIZER = create("stabilizer");

    public static final PartialModel IRON_DRILL = create("iron_drill");
    public static final PartialModel DIAMOND_DRILL = create("diamond_drill");
    public static final PartialModel NETHERITE_DRILL = create("netherite_drill");

    private static PartialModel create(String path) {
        return PartialModel.of(CreateReAutomated.asResource(PATH + path));
    }

    public static void init() {}
}
