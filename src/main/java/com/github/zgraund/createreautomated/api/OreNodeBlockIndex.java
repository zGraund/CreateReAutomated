package com.github.zgraund.createreautomated.api;

import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
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
    public static final SimpleRegistry<Block, IntSupplier> NODE_VALUES = SimpleRegistry.create();
    private static final Set<NonNullSupplier<OreNodeBlock>> BLOCKS = new HashSet<>();

    public static void register(OreNodeBlock block) {
        BLOCKS.add(NonNullSupplier.of(() -> block));
    }

    public static void register(DeferredBlock<OreNodeBlock> node) {
        BLOCKS.add(NonNullSupplier.of(node));
    }

    public static <T extends OreNodeBlock, R> BlockBuilder<T, R> register(@Nonnull BlockBuilder<T, R> builder) {
        BLOCKS.add(builder::getEntry);
        return builder;
    }

    public static int getOrDefaultLimit(Block node) {
        IntSupplier value = NODE_VALUES.get(node);
        return value == null ? 0 : value.getAsInt();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static NonNullSupplier<? extends Block>[] toArray() {
        return BLOCKS.toArray(NonNullSupplier[]::new);
    }
}
