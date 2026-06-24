package com.github.zgraund.createreautomated.api.block;

import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface Extractable {
    default boolean canExtract(int quantity, BlockPos pos, BlockGetter level) {
        return true;
    }

    default void extract(int quantity, BlockPos pos, BlockGetter level) {}

    default float getDrillOffset() {
        return ExtractorBlockEntity.DEFAULT_DRILL_OFFSET;
    }
}
