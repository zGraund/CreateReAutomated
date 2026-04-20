package com.github.zgraund.createreautomated.registry;

import com.github.zgraund.createreautomated.CreateReAutomated;
import com.github.zgraund.createreautomated.block.extractor.ExtractorBlock;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ModArmInteractionPointTypes {
    private static final DeferredRegister<ArmInteractionPointType> ARM_INTERACTION_POINTS =
            DeferredRegister.create(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateReAutomated.MOD_ID);

    static {
        register("extractor", ExtractorType::new);
    }

    private static <T extends ArmInteractionPointType> void register(String name, Supplier<T> type) {
        ARM_INTERACTION_POINTS.register(name, type);
    }

    public static void register(IEventBus eventBus) {
        ARM_INTERACTION_POINTS.register(eventBus);
    }

    public static class ExtractorType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return ModBlocks.EXTRACTOR.has(state) && state.getValue(ExtractorBlock.HALF) == DoubleBlockHalf.UPPER;
        }

        @Override
        public @Nullable ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ExtractorPoint(this, level, pos, state);
        }
    }

    public static class ExtractorPoint extends ArmInteractionPoint {
        public ExtractorPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return mode == Mode.TAKE ? Vec3.atCenterOf(getPos()) : Vec3.upFromBottomCenterOf(getPos(), -(1 / 16f));
        }

        @Override
        protected Direction getInteractionDirection() {
            return mode == Mode.TAKE ? Direction.DOWN : Direction.UP;
        }
    }
}
