package com.github.zgraund.createreautomated.item;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateReAutomated.MOD_ID);

    public static final Supplier<CreativeModeTab> MOD_DEBUG_TAB = CREATIVE_MODE_TAB.register(
            "mod_debug_tab",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(Items.DEBUG_STICK))
                    .title(Component.literal("lo leviamo?"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.BISMUTH);
                        output.accept(ModItems.EXTRACTOR);
                        output.accept(ModItems.DRILLHEAD);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
