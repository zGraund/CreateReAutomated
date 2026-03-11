package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.block.ModBlocks;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public record OreNodeHolder(DeferredBlock<OreNodeBlock> block, List<TagKey<Block>> tags, List<RuleTest> worldGenRules) {
    public void addTags(@Nonnull Function<TagKey<Block>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> func) {
        func.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
        tags.forEach(tag -> func.apply(tag).add(block.get()));
    }

    @Nonnull
    public @Unmodifiable Stream<OreConfiguration.TargetBlockState> getRules() {
        return worldGenRules.stream().map(ruleTest -> OreConfiguration.target(ruleTest, block.get().natural()));
    }

    @Nonnull
    public static @Unmodifiable List<OreConfiguration.TargetBlockState> getAllRules() {
        return ModBlocks.getAllNodes().stream().flatMap(OreNodeHolder::getRules).toList();
    }
}
