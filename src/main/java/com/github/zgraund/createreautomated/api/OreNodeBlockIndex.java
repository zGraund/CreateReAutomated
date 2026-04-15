package com.github.zgraund.createreautomated.api;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Contract;

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
    public static final Set<NonNullSupplier<? extends Block>> BLOCKS = new HashSet<>();

    /**
     * The register method must be called before Block Entities registration
     */
    public static <T extends Block> void register(DeferredBlock<T> node, int yield) {
        BLOCKS.add(NonNullSupplier.of(node));
        NODE_YIELDS.registerProvider(block -> block == node.get() ? () -> yield : null);
    }

    /**
     * The register method must be called before Block Entities registration
     */
    @Nonnull
    @Contract(pure = true)
    public static <T extends Block, R> NonNullUnaryOperator<BlockBuilder<T, R>> register(int yield) {
        return builder -> {
            BLOCKS.add(builder::getEntry);
            NODE_YIELDS.registerProvider(block -> block == builder.getEntry() ? () -> yield : null);
            return builder;
        };
    }

    public static int getYieldOrDefault(Block node) {
        IntSupplier value = NODE_YIELDS.get(node);
        return value == null ? 0 : value.getAsInt();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static NonNullSupplier<? extends Block>[] toArray() {
        return BLOCKS.toArray(NonNullSupplier[]::new);
    }
}
