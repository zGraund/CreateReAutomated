package com.github.zgraund.createreautomated.block.cutter;

import com.github.zgraund.createreautomated.block.node.OreNodeBlock;
import com.github.zgraund.createreautomated.registry.ModItems;
import com.github.zgraund.createreautomated.registry.ModTags;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CutterBlockEntity extends KineticBlockEntity {
    public static final NozzlePath HARVEST_PATH =
            NozzlePath.defaultStart().toY(-1.59)
                      .toPositiveX().toPositiveZ()
                      .toNegativeX().toNegativeZ();
    public static final NozzlePath FRACTURE_PATH =
            NozzlePath.defaultStart().toY(-1.59)
                      // first zig-zag pass
                      .toPositiveX().toRelativeZ(0.215)
                      .toNegativeX().toRelativeZ(0.215)
                      .toPositiveX().toRelativeZ(0.215)
                      .toNegativeX().toRelativeZ(0.215)
                      .toPositiveX()
                      // second zig-zag pass
                      .toNegativeZ().toRelativeX(-0.215)
                      .toPositiveZ().toRelativeX(-0.215)
                      .toNegativeZ().toRelativeX(-0.215)
                      .toPositiveZ().toRelativeX(-0.215)
                      .toNegativeZ();
    private final LerpedFloat nozzleX = LerpedFloat.linear().startWithValue(NozzlePath.STARTING_XZ);
    private final LerpedFloat nozzleY = LerpedFloat.linear().startWithValue(NozzlePath.STARTING_Y);
    private final LerpedFloat nozzleZ = LerpedFloat.linear().startWithValue(NozzlePath.STARTING_XZ);
    private ScrollOptionBehaviour<Mode> mode;
    private int previousIndex = 0;
    private int currentIndex = 0;
    private State state = State.RETURNING;
    private boolean isForceReturning = false;

    public CutterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        // TODO: use translatable
        mode = new ScrollOptionBehaviour<>(Mode.class, Component.literal("Cutting Mode"), this,
                new CenteredSideValueBoxTransform((state, dir) -> {
                    Direction blockDir = state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
                    return dir.getAxis().isHorizontal() && blockDir != dir && blockDir.getOpposite() != dir;
                })
        );
        mode.withCallback(this::onModeChange);
        behaviours.add(mode);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null)
            return;

        if (level.isClientSide()) {
            // when the nozzle is not moving vertically it's "cutting"
            // the block and particles should be spawned
            // (inclined paths are not supported atm)
            if (!mode.get().getPath().isVerticalMovement(currentIndex))
                // TODO: audio
                spawnParticles();
            return;
        }

        // TODO: i don't know if this should just be a recipe so that it can be configured better
        BlockState node = getNode();
        if (!isSpeedRequirementFulfilled() ||
            !isActive() ||
            isForceReturning ||
            node.is(ModTags.Blocks.NON_BREAKABLE) ||
            (mode.get() == Mode.FRACTURE && node.is(ModTags.Blocks.NON_FRACTURABLE)) ||
            (mode.get() == Mode.HARVEST && node.is(ModTags.Blocks.NON_HARVESTABLE))) {
            state = State.RETURNING;
        } else {
            state = State.CUTTING;
            if (currentIndex == 0)
                currentIndex++;
        }

        if (state == State.RETURNING) {
            tickReturn();
        } else {
            tickSegment();
        }
    }

    public void tickReturn() {
        currentIndex = 0;
        if (tickSegment() && isForceReturning) {
            isForceReturning = false;
        }
    }

    public boolean tickSegment() {
        NozzlePath path = mode.get().getPath();
        if (currentIndex != previousIndex) {
            Vec3 segment = path.segments.get(currentIndex);
            float speed = path.isVerticalMovement(currentIndex) ? 0.025f : 0.005f;
            nozzleX.chase(segment.x(), speed, LerpedFloat.Chaser.LINEAR);
            nozzleY.chase(segment.y(), speed, LerpedFloat.Chaser.LINEAR);
            nozzleZ.chase(segment.z(), speed, LerpedFloat.Chaser.LINEAR);
            previousIndex = currentIndex;
        }
        nozzleX.tickChaser();
        nozzleY.tickChaser();
        nozzleZ.tickChaser();
        boolean settled = nozzleX.settled() && nozzleY.settled() && nozzleZ.settled();
        if (settled && state != State.RETURNING) {
            previousIndex = currentIndex;
            currentIndex++;
            if (currentIndex >= path.segments.size()) {
                state = State.RETURNING;
                currentIndex = 0;
                process();
            }
        }
        notifyUpdate();
        return settled;
    }

    public void process() {
        if (level == null || level.isClientSide())
            return;
        BlockPos nodePos = worldPosition.below(2);
        if (mode.get() == Mode.HARVEST) {
            level.destroyBlock(nodePos, true);
        } else {
            level.destroyBlock(nodePos, false);
            level.addFreshEntity(
                    new ItemEntity(level, nodePos.getX(), nodePos.getY(), nodePos.getZ(), ModItems.NODE_FRAGMENT.asStack(9))
            );
        }
    }

    public void spawnParticles() {
        level.addParticle(
                new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), new FluidStack(Fluids.WATER, 1)),
                worldPosition.getX() + getNozzleX(), worldPosition.getY() + getNozzleY(), worldPosition.getZ() + getNozzleZ(),
                0.001, 1, 0.001
        );
    }

    public void onModeChange(int mode) {
        if (level == null)
            return;
        isForceReturning = true;
    }

    public BlockState getNode() {
        if (level == null)
            return Blocks.AIR.defaultBlockState();
        return level.getBlockState(worldPosition.below(2));
    }

    public boolean isActive() {
        return getNode().getBlock() instanceof OreNodeBlock;
    }

    public float getNozzleZ() {
        return nozzleZ.getValue();
    }

    public float getNozzleY() {
        return nozzleY.getValue();
    }

    public float getNozzleX() {
        return nozzleX.getValue();
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.currentIndex = compound.getInt("Path");
        this.previousIndex = currentIndex - 1;
        this.isForceReturning = compound.getBoolean("Resetting");
        this.nozzleX.setValue(compound.getFloat("NozzleX"));
        this.nozzleY.setValue(compound.getFloat("NozzleY"));
        this.nozzleZ.setValue(compound.getFloat("NozzleZ"));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("Path", currentIndex);
        compound.putBoolean("Resetting", isForceReturning);
        compound.putFloat("NozzleX", nozzleX.getValue());
        compound.putFloat("NozzleY", nozzleY.getValue());
        compound.putFloat("NozzleZ", nozzleZ.getValue());
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, -1, 0);
    }

    public enum State {
        IDLE, CUTTING, RETURNING
    }

    public enum Mode implements INamedIconOptions {
        HARVEST(AllIcons.I_FX_SURFACE_ON),
        FRACTURE(AllIcons.I_FILL);

        private final String translationKey;
        private final AllIcons icon;

        // TODO: make own implementation of AllIcons class
        Mode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "createreautomated.cutter.selection_mode." + Lang.asId(name());
        }

        public NozzlePath getPath() {
            if (this == HARVEST)
                return HARVEST_PATH;
            return FRACTURE_PATH;
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }
    }

    public static class NozzlePath {
        public static final double STARTING_XZ = -0.43;
        public static final double STARTING_Y = -1.06;

        public List<Vec3> segments = new ArrayList<>();

        public NozzlePath(Vec3 start) {
            this.segments.add(start);
        }

        @Nonnull
        @Contract(" -> new")
        public static NozzlePath defaultStart() {
            return new NozzlePath(new Vec3(STARTING_XZ, STARTING_Y, STARTING_XZ));
        }

        public boolean isVerticalMovement(int index) {
            if (index == 0)
                return true;
            return segments.get(index).y() < segments.get(index - 1).y();
        }

        public NozzlePath to(double x, double y, double z) {
            this.segments.add(new Vec3(x, y, z));
            return this;
        }

        public NozzlePath toX(double x) {
            Vec3 prev = segments.getLast();
            to(x, prev.y(), prev.z());
            return this;
        }

        public NozzlePath toY(double y) {
            Vec3 prev = segments.getLast();
            to(prev.x(), y, prev.z());
            return this;
        }

        public NozzlePath toZ(double z) {
            Vec3 prev = segments.getLast();
            to(prev.x(), prev.y(), z);
            return this;
        }

        public NozzlePath toRelativeX(double x) {
            Vec3 prev = segments.getLast();
            to(prev.x() + x, prev.y(), prev.z());
            return this;
        }

        public NozzlePath toRelativeY(double y) {
            Vec3 prev = segments.getLast();
            to(prev.x(), prev.y() + y, prev.z());
            return this;
        }

        public NozzlePath toRelativeZ(double z) {
            Vec3 prev = segments.getLast();
            to(prev.x(), prev.y(), prev.z() + z);
            return this;
        }

        // The following methods are shortcuts to move the nozzle to
        // the X/Z limit of the block

        public NozzlePath toPositiveX() {
            return toX(-STARTING_XZ);
        }

        public NozzlePath toNegativeX() {
            return toX(STARTING_XZ);
        }

        public NozzlePath toPositiveZ() {
            return toZ(-STARTING_XZ);
        }

        public NozzlePath toNegativeZ() {
            return toZ(STARTING_XZ);
        }
    }
}
