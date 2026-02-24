package com.github.zgraund.createreautomated.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public record ExtractorRecipe(Ingredient drill, BlockState node, int processingTime, int durabilityLoss, ItemStack result) implements Recipe<ExtractorRecipeInput> {
    @Override
    public boolean matches(@Nonnull ExtractorRecipeInput input, @Nonnull Level level) {
        return drill.test(input.drill()) && node == input.node();
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull ExtractorRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return result;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EXTRACTOR_RECIPE_SERIALIZER.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.EXTRACTOR_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<ExtractorRecipe> {
        public static final MapCodec<ExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("drill").forGetter(ExtractorRecipe::drill),
                BlockState.CODEC.fieldOf("node").forGetter(ExtractorRecipe::node),
                Codec.INT.fieldOf("processingTime").forGetter(ExtractorRecipe::processingTime),
                Codec.INT.fieldOf("durabilityLoss").forGetter(ExtractorRecipe::durabilityLoss),
                ItemStack.CODEC.fieldOf("result").forGetter(ExtractorRecipe::result)
        ).apply(inst, ExtractorRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Nonnull
        @Override
        public MapCodec<ExtractorRecipe> codec() {
            return CODEC;
        }

        @Nonnull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
