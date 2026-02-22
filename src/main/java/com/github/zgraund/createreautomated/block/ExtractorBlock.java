package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.item.ModItems;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ExtractorBlock extends KineticBlock implements IBE<ExtractorBlockEntity>, ICogWheel {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final MapCodec<ExtractorBlock> CODEC = simpleCodec(ExtractorBlock::new);
    public static final VoxelShape SHAPE_UPPER = Stream.of(
            Block.box(0, 1, 0, 16, 15, 16),
            Block.box(0, -16, 0, 3, 1, 3),
            Block.box(0, -16, 13, 3, 1, 16),
            Block.box(13, -16, 13, 16, 1, 16),
            Block.box(13, -16, 0, 16, 1, 3)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape SHAPE_LOWER = Stream.of(
            Block.box(0, 0, 0, 3, 17, 3),
            Block.box(0, 0, 13, 3, 17, 16),
            Block.box(13, 0, 13, 16, 17, 16),
            Block.box(13, 0, 0, 16, 17, 3),
            Block.box(0, 17, 0, 16, 31, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final SpeedLevel MIN_SPEED = SpeedLevel.MEDIUM;

    public ExtractorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    // TODO:
//    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
//        event.registerBlock(
//                Capabilities.ItemHandler.BLOCK,
//                ,
//                (be, context) -> be.capability
//        );
//    }

    @Nonnull
    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player,
                                              @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (!stack.isEmpty() && !stack.is(ModItems.DRILLHEAD))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide())
            return ItemInteractionResult.SUCCESS;

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            pos = pos.relative(Direction.UP);
            state = level.getBlockState(pos);
            if (!state.is(this)) return ItemInteractionResult.CONSUME;
        }
        return onBlockEntityUseItemOn(level, pos, extractor -> {
            if (player.isCrouching()) {
                if (stack.isEmpty()) {
                    ItemStack drill = extractor.drillInv.extractItem(0, 1, false);
                    if (!player.isCreative())
                        player.getInventory().placeItemBackInInventory(drill);
                }
            } else {
                if (stack.isEmpty()) {
                    ItemStack output = extractor.outputInv.getStackInSlot(0);
                    player.getInventory().placeItemBackInInventory(output);
                    extractor.outputInv.setStackInSlot(0, ItemStack.EMPTY);
                } else {
                    ItemStack remainder = extractor.drillInv.insertItem(0, stack.copy(), false);
                    if (!player.isCreative())
                        stack.setCount(remainder.getCount());
                }
            }
            extractor.setChanged();
            extractor.sendData();
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return MIN_SPEED;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, @Nonnull Direction face) {
        return face.equals(Direction.UP);
    }

    @Override
    public boolean isSmallCog() {
        return true;
    }

    @Nonnull
    @Override
    protected BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighborState, @Nonnull LevelAccessor level,
                                     @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction == half.getDirectionToOther()
            && (!neighborState.is(state.getBlock()) || neighborState.getValue(HALF) != half.getOtherHalf())) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context))
            return super.getStateForPlacement(context);
        return null;
    }

    /**
     * Taken from {@link DoublePlantBlock#preventDropFromBottomPart}
     */
    @Nonnull
    @Override
    public BlockState playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        if (!level.isClientSide() && player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos posBelow = pos.below();
            BlockState stateBelow = level.getBlockState(posBelow);
            if (stateBelow.is(state.getBlock()) && stateBelow.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState newStateBelow = stateBelow.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(posBelow, newStateBelow, 35);
                level.levelEvent(player, 2001, posBelow, Block.getId(stateBelow));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nonnull
    @Override
    protected RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nonnull
    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return SHAPE_LOWER;
        return SHAPE_UPPER;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Override
    @Nonnull
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public Class<ExtractorBlockEntity> getBlockEntityClass() {
        return ExtractorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ExtractorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.EXTRACTOR_BE.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, @Nonnull BlockState state) {
        // TODO: only create when state is "upper"
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return null;
        return IBE.super.newBlockEntity(pos, state);
    }
}
