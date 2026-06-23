package com.github.zgraund.createreautomated.block.advancedextractor;

import com.github.zgraund.createreautomated.block.base.AbstractExtractorBlock;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import com.github.zgraund.createreautomated.config.Config;
import com.github.zgraund.createreautomated.config.RecipeModifiers;
import com.github.zgraund.createreautomated.recipe.AdvancedExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipe;
import com.github.zgraund.createreautomated.recipe.ExtractingRecipeInput;
import com.github.zgraund.createreautomated.registry.ModRecipeTypes;
import com.github.zgraund.createreautomated.util.CreateRALang;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
        if (level == null || isVirtual())
            return;
        AuxiliaryLightManager lightManager = level.getAuxLightManager(getBlockPos());
        if (lightManager != null)
            lightManager.setLightAt(getBlockPos(), fluid.getFluidType().getLightLevel());
        notifyUpdate();
    }

    @Override
    public void tickProgress() {
        super.tickProgress();
        if (recipe instanceof AdvancedExtractingRecipe advancedRecipe)
            tank.drain(advancedRecipe.getFluidAmountModified(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void spawnParticles() {
        if (level == null || !isExtracting())
            return;
        if (isEmpty())
            return;

        super.spawnParticles();

        if (AnimationTickHolder.getTicks() % 10 != 0)
            return;

        FluidParticleData data = new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), getFluidStack());
        RandomSource random = level.getRandom();

        float drillOffset = -getDrillOffset(1);
        float offsetDeg = random.nextFloat() * 360;
        Vec3 offset = VecHelper.rotate(new Vec3(0, drillOffset, 0.25f), offsetDeg, Direction.Axis.Y);

        float particlesSpeed = Math.clamp(getProcessingSpeed() / 2, 1, 25);
        float dirRot = getSpeed() < 0 ? -particlesSpeed : particlesSpeed;
        Vec3 direction = VecHelper.rotate(offset, dirRot, Direction.Axis.Y)
                                  .subtract(offset)
                                  .offsetRandom(level.getRandom(), 1 / 32f);

        Vec3 origin = offset.add(Vec3.atBottomCenterOf(worldPosition));

        level.addParticle(data, origin.x, origin.y, origin.z, direction.x, direction.y + 0.2, direction.z);
    }

    public FluidStack getFluidStack() {
        return tank.getFluid();
    }

    public float getFillPercentage() {
        return ((float) tank.getFluidAmount() / tank.getCapacity());
    }

    public boolean isEmpty() {
        return tank.isEmpty();
    }

    @Override
    public boolean failPreConditions() {
        return tank.isEmpty() || super.failPreConditions();
    }

    @Nonnull
    @Override
    public Optional<ExtractingRecipe> getRecipeFor(ExtractingRecipeInput input) {
        Optional<RecipeHolder<AdvancedExtractingRecipe>> holder = ModRecipeTypes.ADVANCED_EXTRACTING.find(input, level);
        return holder.map(RecipeHolder::value);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean sup = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        boolean fluid = containedFluidTooltip(tooltip, isPlayerSneaking, tank);
        return sup || fluid;
    }

    @Override
    protected boolean addRecipeInfoTooltip(List<Component> tooltip) {
        boolean added = super.addRecipeInfoTooltip(tooltip);

        if (!(recipe instanceof AdvancedExtractingRecipe adv))
            return added;

        CreateRALang.text("Fluid: ")
                    .style(ChatFormatting.GRAY)
                    .add(RecipeModifiers.formatModifier(Config.server().recipeModifiers.fluid))
                    .forGoggles(tooltip);

        CreateRALang.text(adv.getFluidAmountModified() + " mB/t")
                    .style(ChatFormatting.GOLD)
                    .forGoggles(tooltip, 1);

        if (Config.client().advancedExtractorInfo.get()) {
            int totalMb = Math.round(adv.getFluidAmountModified() * (adv.getProcessingDurationModified() / getAbsTheoreticalSpeed()));
            CreateRALang.number(totalMb)
                        .text(" mB")
                        .style(ChatFormatting.GOLD)
                        .forGoggles(tooltip, 1);
        }

        return true;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        FluidStack oldFluid = getFluidStack();
        this.tank.readFromNBT(registries, compound);

        if (level == null || !clientPacket)
            return;

        if (getFluidStack() != oldFluid)
            onFluidChanged(getFluidStack());
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        this.tank.writeToNBT(registries, compound);
    }
}
