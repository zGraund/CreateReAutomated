package com.github.zgraund.createreautomated.recipe;

import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record ExtractorRecipe(Ingredient drill, HolderSet<Block> nodeSet, int processingTime, int durabilityLoss,
                              ItemStack result) implements Recipe<ExtractorRecipeInput> {
    @Override
    public boolean matches(ExtractorRecipeInput input, Level level) {
        if (!drill.test(input.drill()))
            return false;
        BlockState blockState = level.getBlockState(input.nodePos());
        if (!nodeSet.contains(blockState.getBlockHolder()))
            return false;
        if (!(blockState.getBlock() instanceof OreNodeBlock nodeBlock))
            return true;
        return nodeBlock.canExtract(1, input.nodePos(), level);
    }

    @Override
    public ItemStack assemble(ExtractorRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    public @Unmodifiable List<ItemStack> getNodes() {
        return nodeSet.stream().map(holder -> {
            ItemStack nodeItem = new ItemStack(holder.value().asItem());
            if (!nodeItem.isEmpty())
                return nodeItem;
            ItemStack placeholder = new ItemStack(Items.BARRIER);
            placeholder.set(DataComponents.CUSTOM_NAME, holder.value().getName());
            return placeholder;
        }).toList();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EXTRACTOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.EXTRACTOR_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<ExtractorRecipe> {
        public static final MapCodec<ExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("drill").forGetter(ExtractorRecipe::drill),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("node").forGetter(ExtractorRecipe::nodeSet),
                ExtraCodecs.POSITIVE_INT.fieldOf("processingTime").forGetter(ExtractorRecipe::processingTime),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("durabilityLoss", 1).forGetter(ExtractorRecipe::durabilityLoss),
                ItemStack.CODEC.fieldOf("result").forGetter(ExtractorRecipe::result)
        ).apply(inst, ExtractorRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<ExtractorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
