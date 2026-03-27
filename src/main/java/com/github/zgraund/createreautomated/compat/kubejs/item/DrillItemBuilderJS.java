package com.github.zgraund.createreautomated.compat.kubejs.item;

import com.github.zgraund.createreautomated.api.DrillPartialIndex;
import com.github.zgraund.createreautomated.compat.kubejs.CRAKubeJSPlugin;
import com.simibubi.create.foundation.item.ItemDescription;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class DrillItemBuilderJS extends ItemBuilder {
    public ResourceLocation partial;

    public DrillItemBuilderJS(ResourceLocation id) {
        super(id);
        maxStackSize = 1;
        maxDamage = 1000;
    }

    public DrillItemBuilderJS withPartial(ResourceLocation partial) {
        this.partial = partial;
        return this;
    }

    @Override
    public Item createObject() {
        Item item = super.createObject();
        CRAKubeJSPlugin.registerItemTooltipModifier(item);
        ItemDescription.useKey(item, "item.createreautomated.drill_head");
        if (partial != null)
            DrillPartialIndex.MODELS.register(item, PartialModel.of(partial));
        return item;
    }
}
