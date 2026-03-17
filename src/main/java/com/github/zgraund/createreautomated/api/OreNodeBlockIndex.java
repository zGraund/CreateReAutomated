package com.github.zgraund.createreautomated.api;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntSupplier;

public class OreNodeBlockIndex {
    /**
     * Registry for all blocks that are considered valid for a OreNodeEntity. All nodes should be registered here.
     */
    public static final SimpleRegistry<Block, IntSupplier> NODE_VALUES = SimpleRegistry.create();
    public static final Set<DeferredBlock<? extends Block>> BLOCKS = new HashSet<>();

    public static void register(DeferredBlock<? extends Block> block) {
        BLOCKS.add(block);
    }

    public static int getOrDefaultLimit(Block node) {
        IntSupplier value = NODE_VALUES.get(node);
        return value == null ? 0 : value.getAsInt();
    }

    @Nonnull
    public static Block[] toArray() {
        return BLOCKS.stream().map(Holder::value).toArray(Block[]::new);
    }
}
