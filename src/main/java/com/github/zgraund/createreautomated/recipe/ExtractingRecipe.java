package com.github.zgraund.createreautomated.recipe;

import com.github.zgraund.createreautomated.api.block.Extractable;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtractingRecipe extends ProcessingRecipe<ExtractingRecipeInput, ExtractingRecipeParams> {
    private final HolderSet<Block> nodes;
    private final int extractionQuantity;
    private final int durabilityCost;

    public ExtractingRecipe(ExtractingRecipeParams params) {
        super(ModRecipeTypes.EXTRACTING, params);
        this.nodes = params.nodes;
        this.extractionQuantity = params.extractionQuantity;
        this.durabilityCost = params.durabilityCost;
    }

    @Override
    public boolean matches(ExtractingRecipeInput input, Level level) {
        if (!getDrill().test(input.drill()))
            return false;
        BlockState blockState = level.getBlockState(input.nodePos());
        if (!getNodes().contains(blockState.getBlockHolder()))
            return false;
        if (!(blockState.getBlock() instanceof Extractable nodeBlock))
            return true;
        return nodeBlock.canExtract(extractionQuantity, input.nodePos(), level);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 6;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public Ingredient getDrill() {
        if (ingredients.isEmpty())
            throw new IllegalStateException("Extracting recipe has no drill!");
        return ingredients.getFirst();
    }

    public int durabilityCost() {
        return durabilityCost;
    }

    public int extractionQuantity() {
        return extractionQuantity;
    }

    public HolderSet<Block> getNodes() {
        if (nodes.size() == 0)
            throw new IllegalStateException("Extracting recipe has no nodes!");
        return nodes;
    }

    public @Unmodifiable List<ItemStack> getNodesAsItemStacks() {
        return nodes.stream().map(holder -> {
            ItemStack nodeItem = new ItemStack(holder.value().asItem());
            if (!nodeItem.isEmpty())
                return nodeItem;
            ItemStack placeholder = new ItemStack(Items.BARRIER);
            placeholder.set(DataComponents.CUSTOM_NAME, holder.value().getName());
            return placeholder;
        }).toList();
    }

    public interface Factory extends ProcessingRecipe.Factory<ExtractingRecipeParams, ExtractingRecipe> {
        ExtractingRecipe create(ExtractingRecipeParams params);
    }

    @SuppressWarnings("unused")
    public static class Builder extends ProcessingRecipeBuilder<ExtractingRecipeParams, ExtractingRecipe, Builder> {
        public Builder(Factory factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected ExtractingRecipeParams createParams() {
            return new ExtractingRecipeParams();
        }

        public Builder nodes(HolderSet<Block> nodes) {
            params.nodes = nodes;
            return this;
        }

        @SuppressWarnings("deprecation")
        public Builder nodes(TagKey<Block> tag) {
            params.nodes = HolderSet.emptyNamed(BuiltInRegistries.BLOCK.holderOwner(), tag);
            return this;
        }

        @SafeVarargs
        public final Builder nodes(BlockEntry<? extends Block>... blocks) {
            params.nodes = HolderSet.direct(blocks);
            return this;
        }

        public Builder durabilityCost(int cost) {
            params.durabilityCost = cost;
            return this;
        }

        public Builder noDurability() {
            params.durabilityCost = 0;
            return this;
        }

        public Builder extract(int quantity) {
            params.extractionQuantity = quantity;
            return this;
        }

        public Builder secAtMaxSpeed(int seconds) {
            duration(seconds * 256 * 20);
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }
    }

    public static class Serializer implements RecipeSerializer<ExtractingRecipe> {
        private final MapCodec<ExtractingRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, ExtractingRecipe> streamCodec;

        public Serializer(ProcessingRecipe.Factory<ExtractingRecipeParams, ExtractingRecipe> factory) {
            this.codec = ProcessingRecipe.codec(factory, ExtractingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, ExtractingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<ExtractingRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtractingRecipe> streamCodec() {
            return streamCodec;
        }
    }
}
