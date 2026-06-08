package com.github.zgraund.createreautomated.block.advancedextractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import com.github.zgraund.createreautomated.recipe.AdvancedExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipeInput;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class AdvancedExtractorBlockEntity extends ExtractorBlockEntity {
    public AdvancedExtractorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    protected final FluidTank tank = new SmartFluidTank(1000, this::onFluidChanged);

    public static <T extends AbstractExtractorBlock<?>> void registerAllCapabilities(RegisterCapabilitiesEvent event, NonNullSupplier<T> block) {
        ExtractorBlockEntity.registerInventoryCapabilities(event, block);
        AdvancedExtractorBlockEntity.registerFluidCapabilities(event, block);
    }

    public static <T extends AbstractExtractorBlock<?>> void registerFluidCapabilities(RegisterCapabilitiesEvent event, NonNullSupplier<T> block) {
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    if (blockEntity instanceof AdvancedExtractorBlockEntity extractor &&
                        AbstractExtractorBlock.isUpper(state) &&
                        AbstractExtractorBlock.isLower(level, pos.below())
                    ) {
                        return extractor.tank;
                    }
                    return null;
                },
                block.get()
        );
    }

    protected void onFluidChanged(FluidStack fluid) {
        if (level == null)
            return;
        AuxiliaryLightManager lightManager = level.getAuxLightManager(getBlockPos());
        if (lightManager != null)
            lightManager.setLightAt(getBlockPos(), getFluid().getFluidType().getLightLevel());
    }

    @Override
    public void tickProgress() {
        super.tickProgress();
        if (recipe instanceof AdvancedExtractingRecipe advancedRecipe)
            tank.drain(advancedRecipe.getFluid().amount(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Nonnull
    @Override
    public Optional<ExtractingRecipe> getRecipeFor(ExtractingRecipeInput input) {
        Optional<RecipeHolder<AdvancedExtractingRecipe>> holder = ModRecipeTypes.ADVANCED_EXTRACTING.find(input, level);
        return holder.map(RecipeHolder::value);
    }

    @Override
    public void spawnParticles() {
        // TODO: liquid particles when extracting
        super.spawnParticles();
    }

    public FluidStack getFluid() {
        return tank.getFluid();
    }

    public float getFillPercentage() {
        return ((float) tank.getFluidAmount() / tank.getCapacity());
    }

    public boolean isEmpty() {
        return tank.isEmpty();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean sup = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        boolean fluid = containedFluidTooltip(tooltip, isPlayerSneaking, tank);
        return sup || fluid;
    }

    @Override
    public boolean failPreConditions() {
        return tank.isEmpty() || super.failPreConditions();
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        FluidStack oldFluid = getFluid();
        this.tank.readFromNBT(registries, compound);

        if (level == null || !clientPacket)
            return;

        if (getFluid() != oldFluid)
            onFluidChanged(getFluid());
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        this.tank.writeToNBT(registries, compound);
    }

}
