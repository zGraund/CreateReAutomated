package com.github.zgraund.createreautomated.datagen;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.datagen.recipe.*;
import com.github.zgraund.createreautomated.ponder.ModPonderPlugin;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = CreateReAutomated.MOD_ID)
public class DataGenerators {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void gatherDataHighPriority(@Nonnull GatherDataEvent event) {
        if (!event.getMods().contains(CreateReAutomated.MOD_ID))
            return;

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModCommonBlockModelGen(packOutput, existingFileHelper));

        CreateReAutomated.REGISTRATE.addDataGenerator(ProviderType.LANG, prov -> {
            BiConsumer<String, String> consumer = prov::add;

            new ModLangGen(consumer).generate();

            PonderIndex.addPlugin(new ModPonderPlugin());
            PonderIndex.getLangAccess().provideLang(CreateReAutomated.MOD_ID, consumer);
        });

        CreateReAutomated.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ModTagGen::items);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void gatherData(@Nonnull GatherDataEvent event) {
        if (!event.getMods().contains(CreateReAutomated.MOD_ID))
            return;

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModExtractingRecipeGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModMechanicalCraftingRecipeGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModSequencedAssemblyRecipeGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModCompactingRecipeGen(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(), new ModDataPackProvider(packOutput, lookupProvider));
    }
}
