package com.github.zgraund.createreautomated.item;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateReAutomated.MOD_ID);

    public static final Supplier<CreativeModeTab> BASE = CREATIVE_MODE_TAB.register(
            "createreautomated",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModItems.EXTRACTOR.get()))
                    .title(Component.translatable("itemGroup.createreautomated.base"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.EXTRACTOR);
                        ModItems.getAllDrills().forEach(output::accept);
                        ModBlocks.getAllNodes().forEach(holder -> output.accept(holder.block()));
                        output.accept(ModItems.COPPER_BIT);
                        output.accept(ModItems.IRON_BIT);
                        output.accept(ModItems.GOLD_BIT);
                        output.accept(ModItems.DIAMOND_BIT);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
