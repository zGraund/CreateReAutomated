package com.github.zgraund.createreautomated.block.advancedextractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.registry.ModBlockEntities;
import com.github.zgraund.createreautomated.registry.ModBlocks;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedExtractorBlock extends AbstractExtractorBlock<AdvancedExtractorBlockEntity> {
    public static final MapCodec<AdvancedExtractorBlock> CODEC = simpleCodec(AdvancedExtractorBlock::new);

    public static BlockState getTop() {
        return getBottom().setValue(HALF, DoubleBlockHalf.UPPER);
    }

    public static BlockState getBottom() {
        return ModBlocks.ADVANCED_EXTRACTOR.getDefaultState();
    }

    public AdvancedExtractorBlock(Properties properties) {
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
                sound = FluidHelper.getEmptySound(be.getFluidStack());
            }
            FluidStack oldFluid = be.getFluidStack().copy();
            if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be)) {
                sound = FluidHelper.getFillSound(oldFluid);
            }
            if (sound != null && !level.isClientSide()) {
                level.playSound(null, be.getBlockPos(), sound, SoundSource.BLOCKS, 0.5f, 0.9f);
                be.notifyUpdate();
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return isUpper(state);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        return lightManager != null ? lightManager.getLightAt(pos) : 0;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public Class<AdvancedExtractorBlockEntity> getBlockEntityClass() {
        return AdvancedExtractorBlockEntity.class;
    }

    @Override
    public BlockEntityType<AdvancedExtractorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.LC_EXTRACTOR_BE.get();
    }
}
