package com.github.zgraund.createreautomated.item;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.ExtractorBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
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
                        output.accept(ModItems.EXTRACTOR);
                        output.accept(set(new ItemStack(ModItems.EXTRACTOR.get())));
                        output.accept(ModItems.DRILLHEAD);
                    })
                    .build()
    );

    // FIXME: This was a test to see if we could make each blockstate a separate item in the creative menu
    @Nonnull
    @Contract("_ -> param1")
    private static ItemStack set(@Nonnull ItemStack item) {
        item.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(ExtractorBlock.HALF, DoubleBlockHalf.UPPER));
        item.set(DataComponents.ITEM_NAME, Component.literal(item.getDisplayName().getString() + " Upper"));
        return item;
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
