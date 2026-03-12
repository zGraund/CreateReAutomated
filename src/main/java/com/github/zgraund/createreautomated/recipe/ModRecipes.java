package com.github.zgraund.createreautomated.recipe;

import com.github.zgraund.createreautomated.CreateReAutomated;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CreateReAutomated.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreateReAutomated.MOD_ID);

    public static final Supplier<RecipeType<ExtractorRecipe>> EXTRACTOR_RECIPE = RECIPE_TYPES.register(
            "extracting",
            () -> RecipeType.simple(CreateReAutomated.asResource("extracting"))
    );
    public static final Supplier<RecipeSerializer<ExtractorRecipe>> EXTRACTOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
            "extracting",
            ExtractorRecipe.Serializer::new
    );

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
