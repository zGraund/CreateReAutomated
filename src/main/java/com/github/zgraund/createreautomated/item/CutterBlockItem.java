package com.github.zgraund.createreautomated.item;

import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public class CutterBlockItem extends BlockItem {
    public CutterBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Nonnull
    @Override
    public InteractionResult place(@Nonnull BlockPlaceContext context) {
        Level level = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face.getOpposite());
        if (level.getBlockState(pos).getBlock() instanceof OreNodeBlock && !level.getBlockState(pos).getValue(OreNodeBlock.STABLE) && face == Direction.UP) {
            if (level.getBlockState(pos.above(2)).canBeReplaced())
                context = BlockPlaceContext.at(context, pos.above(2), face);
            else
                return InteractionResult.FAIL;
        }
        return super.place(context);
    }
}
