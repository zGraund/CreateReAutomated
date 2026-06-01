package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlockEntity;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtractorBlockEntity extends AbstractExtractorBlockEntity {

    public ExtractorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerInventoryCapabilities(event, ModBlocks.EXTRACTOR);
    }
}
