package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.recipe.ExtractorRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import java.util.Optional;

public enum ModRecipeTypes implements IRecipeTypeInfo {
    EXTRACTING(new ExtractorRecipe.Serializer(ExtractorRecipe::new));

    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializer;
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> type;

    ModRecipeTypes(RecipeSerializer<?> serializer) {
        String name = Lang.asId(name());
        this.id = CreateReAutomated.asResource(name);
        this.serializer = ModRegistries.SERIALIZER_REGISTRY.register(name, () -> serializer);
        this.type = ModRegistries.TYPE_REGISTRY.register(name, () -> RecipeType.simple(id));
    }

    public static void register(IEventBus eventBus) {
        ModRegistries.TYPE_REGISTRY.register(eventBus);
        ModRegistries.SERIALIZER_REGISTRY.register(eventBus);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer.get();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    }

    @Nonnull
    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I input, @Nonnull Level level) {
        return level.getRecipeManager().getRecipeFor(getType(), input, level);
    }

    private static class ModRegistries {
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTRY =
                DeferredRegister.create(Registries.RECIPE_TYPE, CreateReAutomated.MOD_ID);
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTRY =
                DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreateReAutomated.MOD_ID);
    }
}
