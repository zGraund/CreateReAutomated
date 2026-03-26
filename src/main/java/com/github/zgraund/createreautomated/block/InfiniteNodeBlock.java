package com.github.zgraund.createreautomated.block;

import com.github.zgraund.createreautomated.api.block.Extractable;
import net.minecraft.world.level.block.Block;

public class InfiniteNodeBlock extends Block implements Extractable {
    public InfiniteNodeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getDrillOffset() {
        return 0.98f;
    }
}
