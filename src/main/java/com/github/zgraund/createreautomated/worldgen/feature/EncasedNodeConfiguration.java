package com.github.zgraund.createreautomated.worldgen.feature;

import com.github.zgraund.createreautomated.config.Worldgen;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.List;

public class EncasedNodeConfiguration extends ReplaceBlockConfiguration {
    public static final Codec<EncasedNodeConfiguration> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter(config -> config.targetStates),
            NeoForgeExtraCodecs.xor(
                    Worldgen.NodeGroup.CODEC.fieldOf("group"),
                    IntProvider.codec(0, 6).fieldOf("faces")
            ).forGetter(config -> config.either)
    ).apply(inst, EncasedNodeConfiguration::new));

    private final Either<Worldgen.NodeGroup, IntProvider> either;

    public EncasedNodeConfiguration(List<OreConfiguration.TargetBlockState> targetStates, Either<Worldgen.NodeGroup, IntProvider> config) {
        super(targetStates);
        this.either = config;
    }

    @Nonnull
    @Contract("_, _ -> new")
    public static EncasedNodeConfiguration of(List<OreConfiguration.TargetBlockState> targetStates, IntProvider faces) {
        return new EncasedNodeConfiguration(targetStates, Either.right(faces));
    }

    @Nonnull
    @Contract("_, _ -> new")
    public static EncasedNodeConfiguration of(List<OreConfiguration.TargetBlockState> targetStates, Worldgen.NodeGroup group) {
        return new EncasedNodeConfiguration(targetStates, Either.left(group));
    }

    public int getFaces(RandomSource random) {
        return either.map(group -> group.getConfig().requiredFaces.get(), supp -> supp.sample(random));
    }
}
