package com.github.zgraund.createreautomated.block.liquidcooledextractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlockEntity;
import com.github.zgraund.createreautomated.registry.ModBlocks;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class LCExtractorBlockEntity extends AbstractExtractorBlockEntity {
    protected int luminosity = 0;
    protected FluidTank tank = new SmartFluidTank(1000, this::onFluidChanged);

    public LCExtractorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerInventoryCapabilities(event, ModBlocks.LC_EXTRACTOR);
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    if (blockEntity instanceof LCExtractorBlockEntity extractor &&
                        AbstractExtractorBlock.isUpper(state) &&
                        AbstractExtractorBlock.isLower(level, pos.below())
                    ) {
                        return extractor.tank;
                    }
                    return null;
                },
                ModBlocks.LC_EXTRACTOR.get()
        );
    }

    protected void onFluidChanged(FluidStack fluid) {
        if (level == null)
            return;
        int light = (int) (fluid.getFluidType().getLightLevel() / 1.5);
        if (luminosity != light && !level.isClientSide()) {
            luminosity = light;
            sendData();
        }
    }

    @Override
    public void tickWork() {
        super.tickWork();
        tank.drain(1, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void spawnParticles() {
        // TODO: liquid particles when extracting
        super.spawnParticles();
    }

    public float getFillPercentage() {
        return ((float) tank.getFluidAmount() / tank.getCapacity());
    }

    @Override
    public boolean failPreConditions() {
        return tank.isEmpty() || super.failPreConditions();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        this.tank.writeToNBT(registries, compound);
        compound.putInt("Luminosity", luminosity);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.tank.readFromNBT(registries, compound);
        int oldLuminosity = luminosity;
        luminosity = compound.getInt("Luminosity");
        if (level != null && clientPacket && luminosity != oldLuminosity)
            level.getLightEngine().checkBlock(getBlockPos());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        // TODO: tooltip
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
