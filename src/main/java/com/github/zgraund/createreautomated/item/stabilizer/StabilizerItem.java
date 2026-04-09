package com.github.zgraund.createreautomated.item.stabilizer;

import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.simibubi.create.foundation.item.CustomUseEffectsItem;
import net.createmod.catnip.data.TriState;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StabilizerItem extends Item implements CustomUseEffectsItem {
    public StabilizerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.mayBuild() && canApply(context.getLevel(), context.getClickedPos())) {
            player.startUsingItem(context.getHand());
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) {
            livingEntity.releaseUsingItem();
            return;
        }
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hitResult.getType() != HitResult.Type.BLOCK || !canApply(level, hitResult.getBlockPos()))
            player.releaseUsingItem();

    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player))
            return stack;
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (canApply(level, hitResult.getBlockPos())) {
            livingEntity.swing(livingEntity.getUsedItemHand());
            BlockState new_state = level.getBlockState(hitResult.getBlockPos()).setValue(OreNodeBlock.STABLE, true);
            level.setBlockAndUpdate(hitResult.getBlockPos(), new_state);
            level.playSound(null, hitResult.getBlockPos(), SoundEvents.TRIAL_SPAWNER_PLACE, SoundSource.BLOCKS);
            stack.shrink(1);
        }
        return stack;
    }

    public boolean canApply(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof OreNodeBlock && !state.getValue(OreNodeBlock.STABLE);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 80;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
        return TriState.TRUE;
    }

    @Override
    public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, RandomSource random) {
        int ticks = entity.getTicksUsingItem();
        if (ticks != getUseDuration(stack, entity) && ticks % 20 == 0) {
            entity.playSound(SoundEvents.CHAIN_PLACE);
        }
        return true;
    }
}
