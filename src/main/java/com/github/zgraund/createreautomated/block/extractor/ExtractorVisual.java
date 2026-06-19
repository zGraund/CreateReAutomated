package com.github.zgraund.createreautomated.block.extractor;

import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ExtractorVisual<T extends ExtractorBlockEntity> extends SingleAxisRotatingVisual<T> implements SimpleDynamicVisual {
    protected final RandomSource random = RandomSource.createNewThreadLocalInstance();
    protected final RotatingInstance drill;
    protected final T be;
    @Nullable
    protected PartialModel model;

    public ExtractorVisual(VisualizationContext context, T blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(blockEntity.hasDrill() ? ModPartialModels.HALF_COG : AllPartialModels.COGWHEEL));
        this.be = blockEntity;
        this.model = blockEntity.getDrillModel();
        this.drill = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(model))
                                        .createInstance()
                                        .setPosition(getVisualPosition())
                                        .setup(blockEntity);
        drill.setVisible(blockEntity.hasDrill());
        drill.setChanged();
    }

    @Override
    public void beginFrame(@Nonnull DynamicVisual.Context ctx) {
        float x = 0;
        float y = -blockEntity.getDrillOffset(ctx.partialTick());
        float z = 0;
        if (blockEntity.isExtracting() && !Minecraft.getInstance().isPaused() && AnimationTickHolder.getTicks() % 2 == 0) {
            float factor = 1 / 32f;
            x = (float) (x + (random.nextFloat() - 0.5) * factor);
            y = (float) (y + (random.nextFloat() - 0.5) * factor);
            z = (float) (z + (random.nextFloat() - 0.5) * factor);
        }
        drill.setPosition(getVisualPosition())
             .nudge(x, y, z)
             .setChanged();
    }

    @Override
    public void update(float pt) {
        super.update(pt);
        drill.setup(be).setChanged();
    }

    @Override
    public void tick(TickableVisual.Context context) {
        super.tick(context);
        setRotatingModel();
        setDrillModel();
    }

    protected void setDrillModel() {
        if (be.hasDrill()) {
            PartialModel newModel = be.getDrillModel();
            if (model != newModel) {
                model = newModel;
                drill.setVisible(true);
                setModel(drill, model);
            }
        } else {
            if (model != null) {
                model = null;
                drill.setVisible(false);
            }
        }
    }

    protected void setRotatingModel() {
        if (be.hasDrill()) {
            setModel(rotatingModel, ModPartialModels.HALF_COG);
        } else {
            setModel(rotatingModel, AllPartialModels.COGWHEEL);
        }
    }

    protected void setModel(RotatingInstance instance, PartialModel model) {
        instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(model))
                           .stealInstance(instance);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(drill);
    }

    @Override
    protected void _delete() {
        super._delete();
        drill.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(drill);
    }
}
