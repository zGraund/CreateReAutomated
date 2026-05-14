package com.github.zgraund.createreautomated.worldgen.feature;

import com.github.zgraund.createreautomated.config.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class EncasedNodeConfiguration extends ReplaceBlockConfiguration {
    public static final Codec<EncasedNodeConfiguration> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter(config -> config.targetStates),
            IntProvider.codec(0, 6).optionalFieldOf("facesOverride").forGetter(config -> config.facesOverride)
    ).apply(inst, EncasedNodeConfiguration::new));

    private final Optional<IntProvider> facesOverride;

    public EncasedNodeConfiguration(List<OreConfiguration.TargetBlockState> targetStates) {
        this(targetStates, Optional.empty());
    }

    public EncasedNodeConfiguration(List<OreConfiguration.TargetBlockState> targetStates, Optional<IntProvider> override) {
        super(targetStates);
        this.facesOverride = override;
    }

    public int getFaces(RandomSource random) {
        return facesOverride.map(provider -> provider.sample(random)).orElse(Config.common().worldgen.requiredFaces.get());
    }
}
