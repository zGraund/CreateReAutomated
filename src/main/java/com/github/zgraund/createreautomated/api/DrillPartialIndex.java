package com.github.zgraund.createreautomated.api;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.simibubi.create.api.registry.SimpleRegistry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class DrillPartialIndex {
    /**
     * Registry for drill models, if a drill has a custom model it should be registered here.
     * If no model is provided a default will be used when rendering.
     */
    public static final SimpleRegistry<Item, PartialModel> MODELS = SimpleRegistry.create();

    public static PartialModel getOrDefaultModel(Item item) {
        PartialModel model = MODELS.get(item);
        return model != null ? model : ModPartialModels.IRON_DRILL;
    }

    public static void register(DeferredItem<Item> drill, PartialModel model) {
        MODELS.registerProvider(item -> item == drill.get() ? model : null);
    }
}
