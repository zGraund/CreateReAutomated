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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateReAutomated.MOD_ID);
    private static final List<DeferredItem<Item>> ALL_DRILLS = new ArrayList<>();

    public static final DeferredItem<Item> EXTRACTOR = ITEMS.register(
            "extractor",
            () -> new DoubleHighBlockItem(ModBlocks.EXTRACTOR.get(), new Item.Properties())
    );
    public static final DeferredItem<Item> DIAMOND_DRILL = registerDefaultDrill("diamond_drill", 256);
    public static final DeferredItem<Item> IRON_DRILL = registerDefaultDrill("iron_drill", 128);
    public static final DeferredItem<Item> COPPER_DRILL = registerDefaultDrill("copper_drill", 64);
    public static final DeferredItem<Item> STONE_DRILL = registerDefaultDrill("stone_drill", 32);

    public static final DeferredItem<Item> DIAMOND_BIT = ITEMS.registerSimpleItem("diamond_bit");
    public static final DeferredItem<Item> GOLD_BIT = ITEMS.registerSimpleItem("gold_bit");
    public static final DeferredItem<Item> IRON_BIT = ITEMS.registerSimpleItem("iron_bit");
    public static final DeferredItem<Item> COPPER_BIT = ITEMS.registerSimpleItem("copper_bit");

    @Nonnull
    public static DeferredItem<Item> registerDefaultDrill(String name, int durability) {
        DeferredItem<Item> toReturn = ITEMS.registerSimpleItem(
                name,
                new Item.Properties().stacksTo(1).durability(durability)
        );
        ALL_DRILLS.add(toReturn);
        return toReturn;
    }

    @Nonnull
    public static Item.Properties defaultNodeItemProperties() {
        return new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1);
    }

    @Nonnull
    @Contract(pure = true)
    public static @UnmodifiableView List<DeferredItem<Item>> getAllDrills() {
        return Collections.unmodifiableList(ALL_DRILLS);
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
