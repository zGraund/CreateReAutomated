package com.github.zgraund.createreautomated.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface Extractable {
    default boolean canExtract(int quantity, BlockPos pos, BlockGetter level) {
        return true;
    }

    default float getDrillOffset() {
        return 0.85f;
    }
}
