package com.github.zgraund.createreautomated.ponder;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;

public class ModPonderPlugin implements PonderPlugin {
    @Nonnull
    @Override
    public String getModId() {
        return CreateReAutomated.MOD_ID;
    }

    @Override
    public void registerScenes(@Nonnull PonderSceneRegistrationHelper<ResourceLocation> registrationHelper) {
        PonderSceneRegistrationHelper<DeferredHolder<?, ?>> helper = registrationHelper.withKeyFunction(DeferredHolder::getId);

        helper.forComponents(ModBlocks.EXTRACTOR)
              .addStoryBoard("extractor", ExtractorScenes::extractor)
              .addStoryBoard("advanced_extractor", ExtractorScenes::advancedExtractor);
        helper.forComponents(ModBlocks.ADVANCED_EXTRACTOR)
              .addStoryBoard("advanced_extractor", ExtractorScenes::advancedExtractor);
    }
}
