package com.github.zgraund.createreautomated.ponder;

import com.github.zgraund.createreautomated.block.extractor.ExtractorBlockEntity;
import com.github.zgraund.createreautomated.item.ModItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class ExtractorScene {
    public static void extractor(@Nonnull SceneBuilder builder, @Nonnull SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("extractor", "Mining Ore Nodes");
        scene.configureBasePlate(0, 0, 5);

        scene.world().setKineticSpeed(util.select().layer(0), -16);
        scene.world().setKineticSpeed(util.select().layer(1), 32);
        scene.world().setKineticSpeed(util.select().layersFrom(2), 32);
        scene.world().showSection(util.select().layer(0), Direction.UP);

        scene.idle(20);

        BlockPos nodePos = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(nodePos), Direction.DOWN);

        scene.idle(20);

        scene.overlay().showText(80)
             .attachKeyFrame()
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(nodePos, Direction.WEST))
             .text("Nodes are too hard to mine by hand");

        scene.idle(90);

        // Show Extractor power input
        scene.world().showSection(util.select().fromTo(4, 1, 2, 5, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(3, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 2, 2, 3, 3, 2), Direction.DOWN);
        scene.idle(10);

        // Show Extractor
        BlockPos extTop = util.grid().at(2, 3, 2);
        BlockPos extBot = util.grid().at(2, 2, 2);
        scene.world().setKineticSpeed(util.select().position(extTop), -32);
        scene.world().showSection(util.select().fromTo(extBot, extTop), Direction.DOWN);

        scene.idle(10);

        scene.overlay().showText(60)
             .attachKeyFrame()
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(extTop, Direction.WEST))
             .text("An Extractor can be used to mine them");

        scene.idle(70);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().centerOf(extBot))
             .text("To work, an Extractor needs a drill of the right strength");

        scene.idle(70);

        scene.overlay().showText(60)
             .placeNearTarget()
             .attachKeyFrame()
             .pointAt(util.vector().blockSurface(extBot, Direction.WEST))
             .text("A drill can be inserted via Right-click...");

        scene.idle(70);

        ItemStack drillStack = new ItemStack(ModItems.DIAMOND_DRILL.get());
        scene.overlay()
             .showControls(util.vector().blockSurface(extBot, Direction.NORTH), Pointing.RIGHT, 60)
             .withItem(drillStack)
             .rightClick();

        scene.idle(70);

        // Show input automation
        Selection inputPower = util.select().fromTo(3, 1, 0, 5, 1, 0);
        Selection inputBelt = util.select().fromTo(2, 1, 0, 2, 1, 1);
        BlockPos funnelIn = util.grid().at(2, 2, 1);

        scene.world().showSection(inputPower, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(inputBelt, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(funnelIn), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(funnelIn, Direction.WEST))
             .text("...or with automation");

        scene.idle(40);

        // Create drill on belt
        BlockPos beltStart = util.grid().at(2, 1, 0);
        scene.world().createItemOnBelt(beltStart, Direction.DOWN, drillStack);

        // Insert drill into extractor
        scene.idle(20);
        scene.world().removeItemsFromBelt(beltStart.relative(Direction.SOUTH));
        scene.world().flapFunnel(funnelIn, false);
        scene.world().modifyBlockEntity(extTop, ExtractorBlockEntity.class, be -> be.setVirtualDrill(drillStack));

        scene.idle(20);

        // Hide input automation
        scene.world().hideSection(util.select().position(funnelIn), Direction.DOWN);
        scene.idle(10);
        scene.world().hideSection(inputBelt, Direction.DOWN);
        scene.idle(10);
        scene.world().hideSection(inputPower, Direction.DOWN);

        scene.idle(40);

        scene.overlay().showText(60)
             .placeNearTarget()
             .attachKeyFrame()
             .pointAt(util.vector().blockSurface(extTop, Direction.WEST))
             .text("The output can be obtained via Right-click...");

        scene.idle(70);

        ItemStack diamondStack = new ItemStack(Items.DIAMOND);
        scene.overlay()
             .showControls(util.vector().blockSurface(extTop, Direction.NORTH), Pointing.RIGHT, 60)
             .withItem(diamondStack)
             .rightClick();

        scene.idle(70);

        // Show output automation
        BlockPos funnelOut = util.grid().at(1, 3, 2);

        scene.world().showSection(util.select().fromTo(0, 1, 3, 0, 1, 5), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 1, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(funnelOut), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(60)
             .placeNearTarget()
             .pointAt(util.vector().blockSurface(funnelOut, Direction.SOUTH))
             .text("...or with automation");

        scene.idle(60);

        scene.world().createItemOnBeltLike(funnelOut.below(2), Direction.DOWN, diamondStack);
        scene.world().flapFunnel(funnelOut, true);

        scene.idle(40);

        scene.markAsFinished();
    }
}
