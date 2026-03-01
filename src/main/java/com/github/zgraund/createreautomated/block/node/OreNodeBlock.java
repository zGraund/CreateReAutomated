package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.Config;
import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nonnull;

public class OreNodeBlock extends Block implements IBE<OreNodeEntity> {
    public static final EnumProperty<Resources> RESOURCES = EnumProperty.create("resources", Resources.class);
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    public static final MapCodec<OreNodeBlock> CODEC = simpleCodec(OreNodeBlock::new);

    public final int MAX_EXTRACTIONS;

    public OreNodeBlock(Properties properties, int maxExtractions) {
        super(properties);
        this.MAX_EXTRACTIONS = maxExtractions;
        this.registerDefaultState(this.defaultBlockState().setValue(RESOURCES, Resources.RICH).setValue(NATURAL, false));
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
        return Resources.getDrillOffset(state);
    }

    public Resources getStateFromQuantity(int quantity) {
        return Resources.fromQuantity(quantity, MAX_EXTRACTIONS);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RESOURCES, NATURAL);
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

    public enum Resources implements StringRepresentable {
        RICH("rich"),
        ABUNDANT("abundant"),
        MEDIUM("medium"),
        SCARCE("scarce"),
        POOR("poor");

        private final String name;

        Resources(String name) {
            this.name = name;
        }

        public static Resources fromQuantity(int quantity, int maxQuantity) {
            float percentage = ((float) quantity / maxQuantity) * 100;
            if (percentage >= 80) return RICH;
            if (percentage >= 60) return ABUNDANT;
            if (percentage >= 40) return MEDIUM;
            if (percentage >= 20) return SCARCE;
            return POOR;
        }

        // TODO: maybe put this in the enum instances?
        public static int getLightLevel(@Nonnull BlockState state) {
            Resources resources = state.getValue(RESOURCES);
            return switch (resources) {
                case RICH -> 8;
                case ABUNDANT -> 6;
                case MEDIUM -> 4;
                case SCARCE -> 2;
                default -> 0;
            };
        }

        public static float getDrillOffset(@Nonnull BlockState state) {
            Resources resources = state.getValue(RESOURCES);
            return switch (resources) {
                case RICH -> 0.8f;
                case ABUNDANT -> 1f;
                case MEDIUM -> 1.2f;
                case SCARCE -> 1.4f;
                case POOR -> 1.6f;
            };
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
