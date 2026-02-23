package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class ModPartialModels {
    public static final PartialModel HALF_COG = block("half_shaft_cogwheel");
    public static final PartialModel DRILL = block("ore_extractor/drill");

    private static PartialModel block(String path) {
        return PartialModel.of(CreateReAutomated.asResource("block/" + path));
    }

    public static void init() {}
}
