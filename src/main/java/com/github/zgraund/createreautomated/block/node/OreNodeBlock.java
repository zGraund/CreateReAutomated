package com.github.zgraund.createreautomated.block.node;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nonnull;

public class OreNodeBlock extends BaseEntityBlock {
    public static final EnumProperty<Resources> RESOURCES = EnumProperty.create("resources", Resources.class);
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    public static final MapCodec<OreNodeBlock> CODEC = simpleCodec(OreNodeBlock::new);

    public OreNodeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(RESOURCES, Resources.RICH).setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RESOURCES, NATURAL);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return;
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
