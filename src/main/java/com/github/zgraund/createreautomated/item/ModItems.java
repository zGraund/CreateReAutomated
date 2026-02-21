package com.github.zgraund.createreautomated.item;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateReAutomated.MOD_ID);

    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRILLHEAD = ITEMS.register(
            "drill_head",
            () -> new Item(new Item.Properties().durability(128))); //Certo che chi identa così le cose è davvero una bella bestia di satana
    public static final DeferredItem<Item> EXTRACTOR = ITEMS.register(
            "extractor",
            () -> new DoubleHighBlockItem(ModBlocks.EXTRACTOR.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
