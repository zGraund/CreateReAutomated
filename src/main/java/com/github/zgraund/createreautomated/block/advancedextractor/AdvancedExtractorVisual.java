package com.github.zgraund.createreautomated.block.advancedextractor;

import com.github.zgraund.createreautomated.block.extractor.ExtractorVisual;
import com.github.zgraund.createreautomated.registry.ModPartialModels;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

public class AdvancedExtractorVisual extends ExtractorVisual<AdvancedExtractorBlockEntity> {
    public AdvancedExtractorVisual(VisualizationContext context, AdvancedExtractorBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        setModel(rotatingModel, AllPartialModels.SHAFT);
    }

    @Override
    protected void setRotatingModel() {
        if (be.hasDrill()) {
            setModel(rotatingModel, ModPartialModels.SHORT_SHAFT);
        } else {
            setModel(rotatingModel, AllPartialModels.SHAFT);
        }
    }
}
