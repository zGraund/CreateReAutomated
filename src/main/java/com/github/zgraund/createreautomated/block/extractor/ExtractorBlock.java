package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.registry.ModBlockEntities;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtractorBlock extends AbstractExtractorBlock<ExtractorBlockEntity> implements ICogWheel {

    public static BlockState getTop() {
        return getBottom().setValue(HALF, DoubleBlockHalf.UPPER);
    }

    public static BlockState getBottom() {
        return ModBlocks.EXTRACTOR.get().defaultBlockState();
    }

    public ExtractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSmallCog() {
        return true;
    }

    @Override
    public Class<ExtractorBlockEntity> getBlockEntityClass() {
        return ExtractorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ExtractorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.EXTRACTOR_BE.get();
    }
}
