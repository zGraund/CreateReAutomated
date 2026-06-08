package com.github.zgraund.createreautomated.recipe;

import com.github.zgraund.createreautomated.api.block.Extractable;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.config.RecipeModifiers;
import com.github.zgraund.createreautomated.registry.ModItems;
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
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtractingRecipe extends ProcessingRecipe<ExtractingRecipeInput, ExtractingRecipeParams> {
    protected static final RecipeModifiers MODIFIERS = Config.server().recipeModifiers;
    private final HolderSet<Block> nodes;
    private final int extractionQuantity;
    private final int durabilityCost;

    public ExtractingRecipe(ExtractingRecipeParams params) {
        this(ModRecipeTypes.EXTRACTING, params);
    }

    public ExtractingRecipe(ModRecipeTypes type, ExtractingRecipeParams params) {
        super(type, params);
        this.nodes = params.nodes;
        this.extractionQuantity = params.extractionQuantity;
        this.durabilityCost = params.durabilityCost;
    }

    @Override
    public boolean matches(ExtractingRecipeInput input, Level level) {
        if (!getDrill().test(input.drill()))
            return false;
        if (input.drill().getMaxDamage() - input.drill().getDamageValue() < getDurabilityCostModified())
            return false;
        BlockState blockState = input.node();
        if (!getNodes().contains(blockState.getBlockHolder()))
            return false;
        if (!(blockState.getBlock() instanceof Extractable nodeBlock))
            return true;
        return nodeBlock.canExtract(getExtractionQuantityModified(), input.nodePos(), level);
    }

    public Ingredient getDrill() {
        if (ingredients.isEmpty())
            throw new IllegalStateException("Extracting recipe has no drill!");
        return ingredients.getFirst();
    }

    public HolderSet<Block> getNodes() {
        if (nodes.size() == 0)
            throw new IllegalStateException("Extracting recipe has no nodes!");
        return nodes;
    }

    public int getProcessingDurationModified() {
        return applyModifier(getProcessingDuration(), MODIFIERS.duration.getF());
    }

    public int getDurabilityCost() {
        return durabilityCost;
    }

    public int getDurabilityCostModified() {
        return applyModifier(getDurabilityCost(), MODIFIERS.durability.getF());
    }

    public int getExtractionQuantity() {
        return extractionQuantity;
    }

    public int getExtractionQuantityModified() {
        return applyModifier(getExtractionQuantity(), MODIFIERS.node.getF());
    }

    public ItemStack applyOutputModifiers(ItemStack stack) {
        stack.setCount(applyModifier(stack.getCount(), MODIFIERS.output.getF()));
        return stack;
    }

    public int applyModifier(int n, float mod) {
        return Math.round(n * mod);
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

    public interface Factory<T extends ExtractingRecipe> extends ProcessingRecipe.Factory<ExtractingRecipeParams, T> {
        T create(ExtractingRecipeParams params);
    }

    @SuppressWarnings("unused")
    public static class Builder<T extends ExtractingRecipe> extends ProcessingRecipeBuilder<ExtractingRecipeParams, T, Builder<T>> {
        public Builder(Factory<T> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected ExtractingRecipeParams createParams() {
            return new ExtractingRecipeParams();
        }

        public Builder<T> nodes(HolderSet<Block> nodes) {
            params.nodes = nodes;
            return self();
        }

        @SuppressWarnings("deprecation")
        public Builder<T> nodes(TagKey<Block> tag) {
            params.nodes = HolderSet.emptyNamed(BuiltInRegistries.BLOCK.holderOwner(), tag);
            return self();
        }

        @SafeVarargs
        public final Builder<T> nodes(BlockEntry<? extends Block>... blocks) {
            params.nodes = HolderSet.direct(blocks);
            return self();
        }

        @SuppressWarnings("deprecation")
        public Builder<T> nodes(Block... blocks) {
            params.nodes = HolderSet.direct(Arrays.stream(blocks).map(Block::builtInRegistryHolder).toList());
            return self();
        }

        public Builder<T> durabilityCost(int cost) {
            params.durabilityCost = cost;
            return self();
        }

        public Builder<T> noDurability() {
            params.durabilityCost = 0;
            return self();
        }

        public Builder<T> fragments(int quantity, float chance) {
            return output(chance, ModItems.NODE_FRAGMENT, quantity);
        }

        public Builder<T> defaultFragments() {
            return fragments(1, 0.01f);
        }

        public Builder<T> extract(int quantity) {
            params.extractionQuantity = quantity;
            return self();
        }

        public Builder<T> secAtMaxSpeed(int seconds) {
            return duration(seconds * 256 * 20);
        }

        @Override
        public Builder<T> self() {
            return this;
        }
    }

    public static class Serializer<T extends ExtractingRecipe> implements RecipeSerializer<T> {
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(ProcessingRecipe.Factory<ExtractingRecipeParams, T> factory) {
            this.codec = ProcessingRecipe.codec(factory, ExtractingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, ExtractingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<T> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec;
        }
    }
}
