package com.github.zgraund.createreautomated.block.liquidcooledextractor;

import com.github.zgraund.createreautomated.block.extractor.ExtractorVisual;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

public class LCExtractorVisual extends ExtractorVisual<LCExtractorBlockEntity> {
    public LCExtractorVisual(VisualizationContext context, LCExtractorBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        setModel(rotatingModel, AllPartialModels.SHAFT);
    }

    @Override
    protected void setRotatingModel() {/* no-op */}
}
