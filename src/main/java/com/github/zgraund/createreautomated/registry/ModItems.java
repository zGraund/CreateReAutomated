package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.api.DrillPartialIndex;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
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

    public static final ItemEntry<Item> NODE_FRAGMENT = simple("node_fragment");

    // maybe register partial model directly in ModPartialModels (?)
    public static final ItemEntry<Item>
            IRON_DRILL = drill("iron_drill", 1500, ModTags.Items.DRILL_TIER_1, ModPartialModels.IRON_DRILL),
            DIAMOND_DRILL = drill("diamond_drill", 4000, ModTags.Items.DRILL_TIER_2, ModPartialModels.DIAMOND_DRILL),
            NETHERITE_DRILL = drill("netherite_drill", 8000, ModTags.Items.DRILL_TIER_3, ModPartialModels.NETHERITE_DRILL);

    public static final ItemEntry<Item>
            COPPER_BIT = simple("copper_bit"),
            IRON_BIT = simple("iron_bit"),
            GOLD_BIT = simple("gold_bit"),
            DIAMOND_BIT = simple("diamond_bit");

    public static final ItemEntry<Item> UNBAKED_DIAMOND = hidden("unbaked_diamond");
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_DIAMOND = sequencedAssembly("incomplete_diamond");

    @Nonnull
    private static ItemEntry<Item> drill(String name, int durability, TagKey<Item> tier, PartialModel model) {
        return REGISTRATE.item(name, Item::new)
                         .properties(p -> p.stacksTo(1).durability(durability))
                         .tag(tier)
                         .model((ctx, prov) -> {
                             prov.generated(ctx);
                             ResourceLocation path = ctx.getId();
                             prov.singleTexture(path.withPrefix(ModPartialModels.PATH).toString(), prov.modLoc("block/extractor/drill"),
                                     "0", path.withPrefix("block/"));
                         })
                         .onRegister(item -> DrillPartialIndex.MODELS.register(item, model))
                         .onRegister(item -> ItemDescription.useKey(item, "item.createreautomated.drill_head"))
                         .register();
    }

    @Nonnull
    private static ItemEntry<Item> simple(String name) {
        return REGISTRATE.item(name, Item::new).register();
    }

    @Nonnull
    private static ItemEntry<Item> hidden(String name) {
        return REGISTRATE.item(name, Item::new).removeTab(ModCreativeModeTab.BASE.getKey()).register();
    }

    @Nonnull
    private static ItemEntry<SequencedAssemblyItem> sequencedAssembly(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                         .removeTab(ModCreativeModeTab.BASE.getKey())
                         .register();
    }

    public static void register() {}
}
