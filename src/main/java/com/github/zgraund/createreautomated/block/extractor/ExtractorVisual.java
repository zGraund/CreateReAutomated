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

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ExtractorVisual extends SingleAxisRotatingVisual<ExtractorBlockEntity> implements SimpleDynamicVisual {
    private final RotatingInstance drill;
    private final ExtractorBlockEntity be;
    @Nullable
    private PartialModel model;

    public ExtractorVisual(VisualizationContext context, ExtractorBlockEntity blockEntity, float partialTick) {
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
    public void beginFrame(DynamicVisual.Context ctx) {
        drill.setPosition(getVisualPosition())
             .nudge(0, -blockEntity.getDrillOffset(), 0)
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
        if (be.hasDrill()) {
            PartialModel newModel = be.getDrillModel();
            if (model != newModel) {
                model = newModel;
                drill.setVisible(true);
                setModel(drill, model);
                setModel(rotatingModel, ModPartialModels.HALF_COG);
            }
        } else {
            if (model != null) {
                model = null;
                drill.setVisible(false);
                setModel(rotatingModel, AllPartialModels.COGWHEEL);
            }
        }
    }

    private void setModel(RotatingInstance instance, PartialModel model) {
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
