package com.github.zgraund.createreautomated.block.extractor;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.DoubleSupplier;

public class DrillingBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<DrillingBehaviour> TYPE = new BehaviourType<>();
    protected final LerpedFloat progress;
    protected DoubleSupplier start = () -> 0f;
    protected DoubleSupplier end = () -> 1f;
    protected Status status = Status.IDLE;

    @Nullable
    protected LerpedFloat bobbing;
    protected DoubleSupplier bobbingSpeed = () -> 0f;
    protected float bobbingTarget = 0.025f;

    protected boolean simulating;
    protected int holdTicks;

    public DrillingBehaviour(SmartBlockEntity be) {
        super(be);
        this.progress = LerpedFloat.linear().chase(start.getAsDouble(), .01f, LerpedFloat.Chaser.LINEAR);
    }

    public DrillingBehaviour from(@Nonnull DoubleSupplier start) {
        this.start = start;
        return this;
    }

    public DrillingBehaviour to(@Nonnull DoubleSupplier end) {
        this.end = end;
        return this;
    }

    public DrillingBehaviour startAt(float at) {
        progress.startWithValue(at);
        return from(() -> at);
    }

    public void setSpeed(double speed) {
        progress.updateChaseSpeed(speed);
    }

    public DrillingBehaviour bobbing(@Nonnull DoubleSupplier speed) {
        this.bobbingSpeed = speed;
        this.bobbing = LerpedFloat.linear().chase(0.05f, speed.getAsDouble(), LerpedFloat.Chaser.LINEAR);
        return this;
    }

    public void setBobbingSpeed(DoubleSupplier speed) {
        this.bobbingSpeed = speed;
    }

    public void start() {
        if (isWorking() || isExtending())
            return;
        status = Status.EXTENDING;
        progress.updateChaseTarget((float) end.getAsDouble());
    }

    public void stop() {
        if (isIdle() || isRetracting() || holdTicks > 0)
            return;
        status = Status.RETRACTING;
        progress.updateChaseTarget((float) start.getAsDouble());
    }

    public void reset() {
        status = Status.IDLE;
        progress.startWithValue(start.getAsDouble());
    }

    public final void simulate(int ticks) {
        if (blockEntity.getLevel() == null || !blockEntity.getLevel().isClientSide())
            return;
        simulating = true;
        holdTicks = ticks;
        reset();
        start();
    }

    @Override
    public void tick() {
        Level level = blockEntity.getLevel();

        if (level != null && level.isClientSide()) {
            if (bobbing != null) {
                if (isWorking()) {
                    bobbing.updateChaseSpeed(bobbingSpeed.getAsDouble());
                    bobbing.tickChaser();
                    if (bobbing.settled()) {
                        bobbing.updateChaseTarget(bobbing.getChaseTarget() == bobbingTarget ? 0 : bobbingTarget);
                    }
                } else {
                    bobbing.setValue(0);
                }
            }

            if (simulating) {
                if (isWorking() && --holdTicks <= 0)
                    stop();

                if (isIdle())
                    simulating = false;
            }
        }

        if (isIdle() || isWorking())
            return;

        progress.tickChaser();
        if (progress.settled())
            status = isRetracting() ? Status.IDLE : Status.WORKING;

        blockEntity.notifyUpdate();
    }

    public boolean isIdle() {
        return status == Status.IDLE;
    }

    public boolean isExtending() {
        return status == Status.EXTENDING;
    }

    public boolean isWorking() {
        return status == Status.WORKING;
    }

    public boolean isRetracting() {
        return status == Status.RETRACTING;
    }

    public float getValue(float partialTicks) {
        float val = progress.getValue(partialTicks);
        if (bobbing != null)
            return val - bobbing.getValue(partialTicks);
        return val;
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (clientPacket && simulating)
            return;
        if (nbt.contains("DrillPosition"))
            progress.readNBT(nbt.getCompound("DrillPosition"), false);
        if (nbt.contains("Status"))
            status = NBTHelper.readEnum(nbt, "Status", Status.class);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        NBTHelper.writeEnum(nbt, "Status", status);
        nbt.put("DrillPosition", progress.writeNBT());
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public enum Status {IDLE, EXTENDING, WORKING, RETRACTING}
}
