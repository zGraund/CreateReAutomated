package com.github.zgraund.createreautomated.block.liquidcooledextractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
        if (!GenericItemEmptying.canItemBeEmptied(level, stack) && !GenericItemFilling.canItemBeFilled(level, stack))
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);

        return onBlockEntityUseItemOn(level, pos, be -> {
            SoundEvent sound = null;
            if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be)) {
                sound = FluidHelper.getEmptySound(be.tank.getFluid());
            }
            if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be)) {
                sound = FluidHelper.getFillSound(be.tank.getFluid());
            }
            if (sound != null && !level.isClientSide()) {
                level.playSound(null, be.getBlockPos(), sound, SoundSource.BLOCKS);
                be.notifyUpdate();
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return getBlockEntityOptional(level, pos).map(be -> be.luminosity).orElse(0);
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
