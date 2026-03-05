package com.github.zgraund.createreautomated.ponder;

import com.github.zgraund.createreautomated.item.ModItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ExtractorScene {
    public static void extractor(@Nonnull SceneBuilder builder, @Nonnull SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("extractor", "Mining Ore Nodes using an Extractor");
        scene.configureBasePlate(0, 0, 5);

        scene.world().setKineticSpeed(util.select().layer(0), -16);
        scene.world().setKineticSpeed(util.select().layer(1), 32);
        scene.world().showSection(util.select().layer(0), Direction.UP);

        scene.idle(20);

        BlockPos nodePos = util.grid().at(2, 1, 2);
        Selection node = util.select().position(nodePos);
        scene.world().showSection(node, Direction.DOWN);

        scene.idle(20);

        scene.overlay().showText(80)
             .attachKeyFrame()
             .placeNearTarget()
             .pointAt(util.vector().topOf(nodePos))
             .text("Nodes are to hard to mine by hand");

        scene.idle(90);

        scene.world().setKineticSpeed(util.select().column(3, 2), 32);
        scene.world().showSection(util.select().fromTo(3, 1, 2, 5, 1, 2), Direction.DOWN);

        scene.idle(10);

        scene.world().showSection(util.select().fromTo(3, 2, 2, 3, 3, 2), Direction.DOWN);

        scene.idle(10);

        BlockPos extTop = util.grid().at(2, 3, 2);
        BlockPos extBot = util.grid().at(2, 2, 2);
        scene.world().setKineticSpeed(util.select().column(2, 2), -32);
        scene.world().showSection(util.select().fromTo(extBot, extTop), Direction.DOWN);

        scene.idle(10);

        scene.overlay().showText(60)
             .attachKeyFrame()
             .placeNearTarget()
             .pointAt(util.vector().topOf(extTop))
             .text("An Extractor can be used to mine them");

        scene.idle(70);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().centerOf(extBot))
             .text("To work, an Extractor need a drill of the right strength");

        scene.idle(70);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(extBot, Direction.WEST))
             .text("A drill can be inserted via Right-click...");

        scene.idle(70);

        scene.overlay()
             .showControls(util.vector().blockSurface(extBot, Direction.NORTH), Pointing.RIGHT, 60)
             .withItem(new ItemStack(ModItems.DRILLHEAD.get()))
             .rightClick();

        scene.idle(70);

        BlockPos inputStart = util.grid().at(2, 1, 0);
        BlockPos inputEnd = util.grid().at(5, 2, 1);
        BlockPos funnelIn = util.grid().at(2, 2, 1);
        scene.world().setKineticSpeed(util.select().fromTo(inputStart, inputEnd), 32);
        scene.world().showSection(util.select().fromTo(3, 1, 0, 5, 1, 0), Direction.DOWN);

        scene.idle(10);

        scene.world().showSection(util.select().fromTo(2, 1, 0, 2, 1, 1), Direction.DOWN);

        scene.idle(10);

        scene.world().showSection(util.select().position(funnelIn), Direction.DOWN);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(funnelIn, Direction.WEST))
             .text("...Or with an automation");

        scene.idle(40);

        scene.world().createItemOnBelt(util.grid().at(2, 1, 0), Direction.DOWN, new ItemStack(ModItems.DRILLHEAD.get()));

        scene.idle(40);

        scene.world().hideSection(util.select().position(funnelIn), Direction.DOWN);

        scene.idle(10);

        scene.world().hideSection(util.select().fromTo(3, 1, 0, 5, 1, 0), Direction.DOWN);

        scene.idle(10);

        scene.world().hideSection(util.select().fromTo(2, 1, 0, 2, 1, 1), Direction.DOWN);

        scene.idle(40);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(extTop, Direction.WEST))
             .text("The output can be obtained via Right-click...");

        scene.idle(70);

        scene.overlay()
             .showControls(util.vector().blockSurface(extTop, Direction.NORTH), Pointing.RIGHT, 60)
             .rightClick();

        scene.idle(70);

        BlockPos funnelOut = util.grid().at(1, 3, 2);
        scene.world().setKineticSpeed(util.select().fromTo(0, 1, 2, 1, 5, 1), 32);
        scene.world().showSection(util.select().fromTo(0, 1, 3, 0, 1, 5), Direction.DOWN);

        scene.idle(10);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 1, 1, 2), Direction.DOWN);

        scene.idle(10);

        scene.world().showSection(util.select().position(funnelOut), Direction.DOWN);

        scene.idle(40);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(funnelOut, Direction.SOUTH))
             .text("...Or with an automation");

        // TODO: add item inside output inv
        scene.idle(60);

        scene.markAsFinished();
    }
}
