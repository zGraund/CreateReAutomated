package com.github.zgraund.createreautomated.block.node;

import com.github.zgraund.createreautomated.api.OreNodeBlockIndex;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.registry.ModBlockEntities;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreNodeBlock extends Block implements IBE<OreNodeEntity> {
    public static final IntegerProperty DEPLETION = IntegerProperty.create("depletion", 0, 10);
    public static final BooleanProperty STABLE = BooleanProperty.create("stable");

    public final BlockState baseRock;

    public OreNodeBlock(Properties properties, BlockState turnsInto) {
        super(properties);
        this.baseRock = turnsInto;
        this.registerDefaultState(this.defaultBlockState().setValue(DEPLETION, 0).setValue(STABLE, true));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (stack.is(ModBlocks.STABILIZER_CAGE.asItem()) && !state.getValue(STABLE)) {
            if (!player.isCreative())
                stack.shrink(1);
            // TODO: choose better sound
            level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_PLACE, SoundSource.BLOCKS, 1, 0.80f);
            level.setBlockAndUpdate(pos, state.setValue(STABLE, true));
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player.getMainHandItem().isEmpty() && Config.client().debugOreNodeOverlay.get()) {
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

    public int getStateFromQuantity(int quantity) {
        int percentage = (100 * quantity) / getMaxExtractions();
        return Math.min(10, (100 - percentage + 9) / 10);
    }

    public boolean isInfinite() {
        return getMaxExtractions() <= 0;
    }

    public int getMaxExtractions() {
        return OreNodeBlockIndex.getOrDefaultLimit(this);
    }

    public BlockState unstable() {
        return this.defaultBlockState().setValue(STABLE, false);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!state.getValue(STABLE) && random.nextInt(10) == 0) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (!level.getBlockState(neighbor).isSolidRender(level, neighbor)) {
                    // TODO: choose better particle
                    ParticleUtils.spawnParticleOnFace(level, pos, dir,
                            new DustParticleOptions(new Vector3f(255, 0, 208), 0.75f), Vec3.ZERO, 0.57);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BlockItemStateProperties data = stack.getComponents().get(DataComponents.BLOCK_STATE);
        if (data != null) {
            Integer level = data.get(OreNodeBlock.DEPLETION);
            if (level != null && level != 0) {
                // TODO: use translatable here
                tooltipComponents.add(
                        Component.literal("Depletion Level: ")
                                 .withStyle(ChatFormatting.GRAY)
                                 .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.DARK_GRAY))
                );
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DEPLETION, STABLE);
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
        return isInfinite() ? null : IBE.super.newBlockEntity(pos, state);
    }
}
