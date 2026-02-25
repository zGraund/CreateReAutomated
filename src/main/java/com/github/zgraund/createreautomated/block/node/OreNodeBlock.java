package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.Config;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;

public class OreNodeBlock extends BaseEntityBlock {
    public static final EnumProperty<Resources> RESOURCES = EnumProperty.create("resources", Resources.class);
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    public static final MapCodec<OreNodeBlock> CODEC = simpleCodec(OreNodeBlock::new);

    private final int MAX_EXTRACTIONS;

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

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RESOURCES, NATURAL);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return isInfinite() ? null : new OreNodeEntity(pos, state).setRemaining(MAX_EXTRACTIONS);
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
    protected MapCodec<? extends BaseEntityBlock> codec() {
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

        @Nonnull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
