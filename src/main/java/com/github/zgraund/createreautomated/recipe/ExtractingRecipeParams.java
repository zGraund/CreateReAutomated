package com.github.zgraund.createreautomated.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class ExtractingRecipeParams extends ProcessingRecipeParams {
    public static final MapCodec<ExtractingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            codec(ExtractingRecipeParams::new).forGetter(Function.identity()),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("node").forGetter(ExtractingRecipeParams::nodes),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("durabilityCost").forGetter(ExtractingRecipeParams::durabilityCost)
    ).apply(inst, (params, nodes, durability) -> {
        params.nodes = nodes;
        params.durabilityCost = durability;
        return params;
    }));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtractingRecipeParams> STREAM_CODEC = streamCodec(ExtractingRecipeParams::new);

    protected HolderSet<Block> nodes;
    protected int durabilityCost;

    protected ExtractingRecipeParams() {
        this.nodes = HolderSet.empty();
        this.durabilityCost = 1;
    }

    protected final HolderSet<Block> nodes() {
        return nodes;
    }

    protected final int durabilityCost() {
        return durabilityCost;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.holderSet(Registries.BLOCK).encode(buffer, nodes);
        ByteBufCodecs.INT.encode(buffer, durabilityCost);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        nodes = ByteBufCodecs.holderSet(Registries.BLOCK).decode(buffer);
        durabilityCost = ByteBufCodecs.INT.decode(buffer);
    }
}
