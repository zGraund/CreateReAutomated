package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.Config;
import com.github.zgraund.createreautomated.block.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreNodeBlock extends Block implements IBE<OreNodeEntity> {
//    private static final List<OreNodeBlock> ALL_NODES = new ArrayList<>();

    public static final EnumProperty<DepletionLevel> DEPLETION = EnumProperty.create("depletion", DepletionLevel.class);
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    public static final MapCodec<OreNodeBlock> CODEC = simpleCodec(OreNodeBlock::new);

    public final int MAX_EXTRACTIONS;

    public OreNodeBlock(Properties properties, int maxExtractions) {
        super(properties);
        this.MAX_EXTRACTIONS = maxExtractions;
        this.registerDefaultState(this.defaultBlockState().setValue(DEPLETION, DepletionLevel.ZERO).setValue(NATURAL, false));
//        ALL_NODES.add(this);
    }

//    @Contract(pure = true)
//    public static @UnmodifiableView List<OreNodeBlock> getAllNodes() {
//        return Collections.unmodifiableList(ALL_NODES);
//    }
//
//    public static OreNodeBlock[] toArray() {
//        return ALL_NODES.toArray(new OreNodeBlock[0]);
//    }

    public OreNodeBlock(Properties properties) {
        this(properties, 0);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && Config.Client.DEBUG_ORE_NODE_DISPLAY.get()) {
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

    public float getDrillOffset() {
        return 0.85f;
    }

    public DepletionLevel getStateFromQuantity(int quantity) {
        return DepletionLevel.fromQuantity(quantity, MAX_EXTRACTIONS);
    }

    public BlockState natural() {
        return this.defaultBlockState().setValue(NATURAL, true);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BlockItemStateProperties data = stack.getComponents().get(DataComponents.BLOCK_STATE);
        if (data != null) {
            OreNodeBlock.DepletionLevel level = data.get(OreNodeBlock.DEPLETION);
            if (level != null && level != OreNodeBlock.DepletionLevel.ZERO) {
                // TODO: use translatable here
                tooltipComponents.add(
                        Component.literal("Depletion Level: ")
                                 .withStyle(ChatFormatting.GRAY)
                                 .append(Component.literal(level.getSerializedName()).withStyle(ChatFormatting.DARK_GRAY))
                );
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return isInfinite() ? null : new OreNodeEntity(pos, state);
    }

    public boolean isInfinite() {
        return MAX_EXTRACTIONS <= 0;
    }

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

        @Override
        public String getSerializedName() {
            return String.valueOf(this.stage);
        }
    }
}
