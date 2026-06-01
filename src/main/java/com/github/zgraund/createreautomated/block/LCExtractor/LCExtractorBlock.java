package com.github.zgraund.createreautomated.block.LCExtractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LCExtractorBlock extends AbstractExtractorBlock<LCExtractorBlockEntity> {
    public static final MapCodec<LCExtractorBlock> CODEC = simpleCodec(LCExtractorBlock::new);

    public LCExtractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        // TODO:
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public Class<LCExtractorBlockEntity> getBlockEntityClass() {
        return LCExtractorBlockEntity.class;
    }

    @Override
    public BlockEntityType<LCExtractorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.LC_EXTRACTOR_BE.get();
    }
}
