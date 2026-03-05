package com.github.zgraund.createreautomated.item;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateReAutomated.MOD_ID);

    public static final DeferredItem<Item> DRILLHEAD = ITEMS.registerSimpleItem(
            "drill_head",
            new Item.Properties().durability(128).stacksTo(1));
    public static final DeferredItem<Item> EXTRACTOR = ITEMS.register(
            "extractor",
            () -> new DoubleHighBlockItem(ModBlocks.EXTRACTOR.get(), new Item.Properties())
    );

    @Nonnull
    public static Item.Properties defaultNodeItemProperties() {
        return new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

        // Create tooltip modifiers
        ITEMS.getEntries().forEach(deferredItem ->
                TooltipModifier.REGISTRY.registerProvider(item -> {
                    TooltipModifier modifier =
                            new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
                    return item == deferredItem.get() ? modifier : null;
                })
        );
    }
}
