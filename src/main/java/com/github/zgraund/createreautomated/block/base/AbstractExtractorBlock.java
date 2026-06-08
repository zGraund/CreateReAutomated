package com.github.zgraund.createreautomated.block.base;

import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.registry.ModTags;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractExtractorBlock<T extends ExtractorBlockEntity> extends KineticBlock implements IBE<T> {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
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

    public AbstractExtractorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    public static boolean isUpper(Level level, BlockPos pos) {
        return isUpper(level.getBlockState(pos));
    }

    public static boolean isUpper(BlockState state) {
        return state.getBlock() instanceof AbstractExtractorBlock<?> && state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    public static boolean isLower(Level level, BlockPos pos) {
        return isLower(level.getBlockState(pos));
    }

    public static boolean isLower(BlockState state) {
        return state.getBlock() instanceof AbstractExtractorBlock<?> && state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (!stack.isEmpty() && !stack.is(ModTags.Items.DRILLS))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide())
            return ItemInteractionResult.SUCCESS;

        if (isLower(state)) {
            pos = pos.above();
            if (!isUpper(level.getBlockState(pos))) return ItemInteractionResult.FAIL;
        }

        return onBlockEntityUseItemOn(level, pos, extractor -> {
            if (player.isCrouching()) {
                if (stack.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(extractor.extractDrill());
                }
            } else {
                if (stack.isEmpty()) {
                    extractor.extractOutput().forEach(player.getInventory()::placeItemBackInInventory);
                } else {
                    stack.setCount(extractor.insertDrill(stack).getCount());
                }
            }
            extractor.notifyUpdate();
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        Supplier<SpeedLevel> speed = Config.server().extractorConfig.getExtractorSpeed(this);
        return speed == null ? SpeedLevel.MEDIUM : speed.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.equals(Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction == half.getDirectionToOther()
            && (!neighborState.is(state.getBlock()) || neighborState.getValue(HALF) != half.getOtherHalf())) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        super.setPlacedBy(level, pos.above(), state, placer, stack);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context))
            return super.getStateForPlacement(context);
        return null;
    }

    /**
     * Taken from {@link DoublePlantBlock#preventDropFromBottomPart}
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
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

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_LOWER : SHAPE_UPPER;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? null : IBE.super.newBlockEntity(pos, state);
    }
}
