package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.Config;
import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class OreNodeBlock extends Block implements IBE<OreNodeEntity> {
    public static final EnumProperty<DepletionLevel> DEPLETION = EnumProperty.create("depletion", DepletionLevel.class);
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    public static final MapCodec<OreNodeBlock> CODEC = simpleCodec(OreNodeBlock::new);

    public final int MAX_EXTRACTIONS;

    public OreNodeBlock(Properties properties, int maxExtractions) {
        super(properties);
        this.MAX_EXTRACTIONS = maxExtractions;
        this.registerDefaultState(this.defaultBlockState().setValue(DEPLETION, DepletionLevel.ZERO).setValue(NATURAL, false));
    }

    public OreNodeBlock(Properties properties) {
        this(properties, 0);
    }

    @Nonnull
    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player,
                                               @Nonnull BlockHitResult hitResult) {
        if (!level.isClientSide() && Config.DEBUG_ORE_NODE_DISPLAY.get()) {
            player.sendSystemMessage(Component.literal(
                    level.getBlockEntity(pos) instanceof OreNodeEntity oreNode
                            ? "The remaining ore is: " + oreNode.getRemaining()
                            : "The node is unlimited"
            ));
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    public boolean canExtract(int quantity, BlockPos pos, BlockGetter level) {
        return getBlockEntityOptional(level, pos)
                .map(be -> be.canExtract(quantity))
                .orElse(this.isInfinite());
    }

    public float getRemainingPercentage(int remaining) {
        return (float) remaining / MAX_EXTRACTIONS;
    }

    public float getDrillOffset(@Nonnull BlockState state) {
        return 0.85f;
    }

    public DepletionLevel getStateFromQuantity(int quantity) {
        return DepletionLevel.fromQuantity(quantity, MAX_EXTRACTIONS);
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(NATURAL) && random.nextInt(10) == 0) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (!level.getBlockState(neighbor).isSolidRender(level, neighbor)) {
                    ParticleUtils.spawnParticleOnFace(level, pos, dir, ParticleTypes.ELECTRIC_SPARK, Vec3.ZERO, 0.57);
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DEPLETION, NATURAL);
    }

    @Override
    public Class<OreNodeEntity> getBlockEntityClass() {
        return OreNodeEntity.class;
    }

    @Override
    public BlockEntityType<? extends OreNodeEntity> getBlockEntityType() {
        return ModBlockEntities.ORE_NODE_BE.get();
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return isInfinite() ? null : new OreNodeEntity(pos, state);
    }

    public boolean isInfinite() {
        return MAX_EXTRACTIONS <= 0;
    }

    @Nonnull
    @Override
    protected RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nonnull
    @Override
    protected MapCodec<OreNodeBlock> codec() {
        return CODEC;
    }

    public enum DepletionLevel implements StringRepresentable {
        ZERO(0),
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10);

        private final int stage;
        private final int light;

        DepletionLevel(int stage) {
            this.stage = stage;
            this.light = 10 - stage;
        }

        public static DepletionLevel fromQuantity(int quantity, int maxQuantity) {
            int percentage = (100 * quantity) / maxQuantity;
            if (percentage > 99) return ZERO;
            if (percentage > 90) return ONE;
            if (percentage > 80) return TWO;
            if (percentage > 70) return THREE;
            if (percentage > 60) return FOUR;
            if (percentage > 50) return FIVE;
            if (percentage > 40) return SIX;
            if (percentage > 30) return SEVEN;
            if (percentage > 20) return EIGHT;
            if (percentage > 10) return NINE;
            return TEN;
        }

        public int getLightLevel() {
            return this.light;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return String.valueOf(this.stage);
        }
    }
}
