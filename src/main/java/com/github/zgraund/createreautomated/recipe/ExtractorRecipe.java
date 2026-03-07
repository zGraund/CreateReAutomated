package com.github.zgraund.createreautomated.recipe;

import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public record ExtractorRecipe(Ingredient drill, HolderSet<Block> nodeSet, int processingTime, int durabilityLoss,
                              ItemStack result) implements Recipe<ExtractorRecipeInput> {
    @Override
    public boolean matches(@Nonnull ExtractorRecipeInput input, @Nonnull Level level) {
        if (!drill.test(input.drill()))
            return false;
        BlockState blockState = level.getBlockState(input.nodePos());
        if (!nodeSet.contains(blockState.getBlockHolder()))
            return false;
        if (!(blockState.getBlock() instanceof OreNodeBlock nodeBlock))
            return true;
        return nodeBlock.canExtract(1, input.nodePos(), level);
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
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("node").forGetter(ExtractorRecipe::nodeSet),
//                Codec.either(
//                             BuiltInRegistries.BLOCK.byNameCodec(),
//                             BuiltInRegistries.BLOCK.byNameCodec().listOf()
//                     ).flatComapMap(
//                             List::of,
//                             list -> list.size() != 1 ? DataResult.error(() -> "List: " + list + " not of size 1") : DataResult.success(list.getFirst()))
//                     .fieldOf("node")
//                     .forGetter(ExtractorRecipe::node),
                ExtraCodecs.POSITIVE_INT.fieldOf("processingTime").forGetter(ExtractorRecipe::processingTime),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("durabilityLoss", 1).forGetter(ExtractorRecipe::durabilityLoss),
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
