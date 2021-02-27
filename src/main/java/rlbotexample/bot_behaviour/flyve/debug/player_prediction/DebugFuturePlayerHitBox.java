package rlbotexample.bot_behaviour.flyve.debug.player_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class DebugFuturePlayerHitBox extends FlyveBot {

    private HitBox hitBox;
    private Vector3 hitBoxCenterPosition;

    public DebugFuturePlayerHitBox() {
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        Vector3 opponentNoseOrientation = hitBox.frontOrientation;
        Vector3 opponentRoofOrientation = hitBox.roofOrientation;
        Vector3 opponentRightOrientation = opponentNoseOrientation.crossProduct(opponentRoofOrientation);

        Vector3 hitBoxCorner111 = hitBox.closestPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPositionOfHitBox));
        Vector3 hitBoxCorner110 = hitBox.closestPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPositionOfHitBox));
        Vector3 hitBoxCorner101 = hitBox.closestPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPositionOfHitBox));
        Vector3 hitBoxCorner100 = hitBox.closestPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPositionOfHitBox));
        Vector3 hitBoxCorner011 = hitBox.closestPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPositionOfHitBox));
        Vector3 hitBoxCorner010 = hitBox.closestPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPositionOfHitBox));
        Vector3 hitBoxCorner001 = hitBox.closestPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPositionOfHitBox));
        Vector3 hitBoxCorner000 = hitBox.closestPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPositionOfHitBox));

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner111.toFlatVector(), hitBoxCorner110.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner111.toFlatVector(), hitBoxCorner101.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner111.toFlatVector(), hitBoxCorner011.toFlatVector());

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner010.toFlatVector(), hitBoxCorner011.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner010.toFlatVector(), hitBoxCorner000.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner010.toFlatVector(), hitBoxCorner110.toFlatVector());

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner001.toFlatVector(), hitBoxCorner000.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner001.toFlatVector(), hitBoxCorner011.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner001.toFlatVector(), hitBoxCorner101.toFlatVector());

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner100.toFlatVector(), hitBoxCorner101.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner100.toFlatVector(), hitBoxCorner110.toFlatVector());
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner100.toFlatVector(), hitBoxCorner000.toFlatVector());
    }
}
