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
    public void registerScenes(@Nonnull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<DeferredHolder<?, ?>> HELPER = helper.withKeyFunction(DeferredHolder::getId);

        HELPER.forComponents(ModBlocks.EXTRACTOR).addStoryBoard("extractor", ExtractorScene::extractor);
    }
}
