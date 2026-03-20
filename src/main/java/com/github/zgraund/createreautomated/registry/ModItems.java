package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.DrillPartialIndex;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;

public class ModItems {
    private static final CreateRegistrate REGISTRATE = CreateReAutomated.REGISTRATE;

    // TODO: register partial model directly in ModPartialModels (?)
    public static final ItemEntry<Item>
            IRON_DRILL = drill("iron_drill", 1500, ModTags.Items.DRILL_TIER_1, ModPartialModels.IRON_DRILL),
            DIAMOND_DRILL = drill("diamond_drill", 4000, ModTags.Items.DRILL_TIER_2, ModPartialModels.DIAMOND_DRILL),
            NETHERITE_DRILL = drill("netherite_drill", 8000, ModTags.Items.DRILL_TIER_3, ModPartialModels.NETHERITE_DRILL);

    public static final ItemEntry<Item>
            COPPER_BIT = bit("copper_bit"),
            IRON_BIT = bit("iron_bit"),
            GOLD_BIT = bit("gold_bit"),
            DIAMOND_BIT = bit("diamond_bit");

    @Nonnull
    private static ItemEntry<Item> drill(String name, int durability, TagKey<Item> tier, PartialModel model) {
        return REGISTRATE.item(name, Item::new)
                         .properties(p -> p.stacksTo(1).durability(durability))
                         .tag(tier)
                         .model((ctx, prov) -> {
                             prov.generated(ctx);
                             ResourceLocation path = ctx.getId();
                             prov.withExistingParent(path.withPrefix(ModPartialModels.PATH).toString(), prov.modLoc("block/extractor/drill"))
                                 .texture("0", path.withPrefix("block/"));
                         })
                         .onRegister(item -> DrillPartialIndex.MODELS.register(item, model))
                         .onRegister(item -> ItemDescription.useKey(item, "item.createreautomated.drill_head"))
                         .register();
    }

    @Nonnull
    private static ItemEntry<Item> bit(String name) {
        return REGISTRATE.item(name, Item::new).register();
    }

    public static void register() {}
}
