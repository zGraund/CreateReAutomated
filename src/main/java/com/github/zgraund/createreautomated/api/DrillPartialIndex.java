package com.github.zgraund.createreautomated.api;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.simibubi.create.api.registry.SimpleRegistry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class DrillPartialIndex {
    public static final SimpleRegistry<Item, PartialModel> DRILL_PARTIAL_MODELS = SimpleRegistry.create();

    public static PartialModel getOrDefault(Item item) {
        PartialModel model = DRILL_PARTIAL_MODELS.get(item);
        return model != null ? model : ModPartialModels.IRON_DRILL;
    }

    public static void register(DeferredItem<Item> drill, PartialModel model) {
        DRILL_PARTIAL_MODELS.registerProvider(item -> item == drill.get() ? model : null);
    }
}
