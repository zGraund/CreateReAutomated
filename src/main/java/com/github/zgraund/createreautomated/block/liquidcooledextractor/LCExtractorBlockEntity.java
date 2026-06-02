package com.github.zgraund.createreautomated.block.liquidcooledextractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlockEntity;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class LCExtractorBlockEntity extends AbstractExtractorBlockEntity {
    protected FluidTank tank = new SmartFluidTank(1000, (fluid) -> {});

    public LCExtractorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerInventoryCapabilities(event, ModBlocks.LC_EXTRACTOR);
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    if (state.getValue(AbstractExtractorBlock.HALF) == DoubleBlockHalf.LOWER) {
                        pos = pos.above();
                        state = level.getBlockState(pos);
                        blockEntity = level.getBlockEntity(pos);
                    }
                    if (state.getValue(AbstractExtractorBlock.HALF) == DoubleBlockHalf.UPPER &&
                        blockEntity instanceof LCExtractorBlockEntity extractor)
                        return extractor.tank;
                    return null;
                },
                ModBlocks.LC_EXTRACTOR.get()
        );
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void tickWork() {
        super.tickWork();
        tank.drain(1, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void spawnParticles() {
        super.spawnParticles();
    }

    @Override
    public boolean failPreConditions() {
        return tank.isEmpty() || super.failPreConditions();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        this.tank.writeToNBT(registries, compound);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.tank.readFromNBT(registries, compound);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        // TODO: tooltip
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
