package com.github.zgraund.createreautomated.item;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.simibubi.create.foundation.item.ItemDescription;
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
            "base_tab",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModItems.EXTRACTOR.get()))
                    .title(Component.translatable("itemGroup.createreautomated.base"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.EXTRACTOR);
                        output.accept(ModItems.DRILLHEAD);
                        OreNodeBlock.getAllNodes().forEach(nodeBlock -> {
                            // FIXME: very scuffed hack to register the key after block and item have been registered,
                            //        need to find a better way
                            ItemDescription.useKey(nodeBlock, "block.createreautomated.ore_node");
                            output.accept(nodeBlock);
                        });
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
