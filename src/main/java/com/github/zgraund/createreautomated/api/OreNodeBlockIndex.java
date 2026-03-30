package com.github.zgraund.createreautomated.api;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntSupplier;

public class OreNodeBlockIndex {
    /**
     * Registry for all blocks that are considered valid for a OreNodeEntity. All nodes
     * and their max number of extractions should be registered here.
     */
    public static final SimpleRegistry<Block, IntSupplier> NODE_YIELDS = SimpleRegistry.create();
    private static final Set<NonNullSupplier<? extends Block>> BLOCKS = new HashSet<>();

    public static <T extends Block> void register(T block) {
        BLOCKS.add(NonNullSupplier.of(() -> block));
    }

    public static <T extends Block> void register(DeferredBlock<T> node) {
        BLOCKS.add(NonNullSupplier.of(node));
    }

    public static void register(ResourceLocation id) {
        BLOCKS.add(NonNullSupplier.of(() -> BuiltInRegistries.BLOCK.get(id)));
    }

    public static <T extends Block, R> BlockBuilder<T, R> register(@Nonnull BlockBuilder<T, R> builder) {
        BLOCKS.add(builder::getEntry);
        return builder;
    }

    public static int getOrDefaultLimit(Block node) {
        IntSupplier value = NODE_YIELDS.get(node);
        return value == null ? 0 : value.getAsInt();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static NonNullSupplier<? extends Block>[] toArray() {
        return BLOCKS.toArray(NonNullSupplier[]::new);
    }
}
