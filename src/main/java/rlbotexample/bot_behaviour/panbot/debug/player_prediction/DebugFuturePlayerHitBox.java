package rlbotexample.bot_behaviour.panbot.debug.player_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.HitBox;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class DebugFuturePlayerHitBox extends PanBot {

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

        Vector3 hitBoxCorner111 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner110 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner101 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner100 = hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner011 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner010 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner001 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(hitBox.centerPosition));
        Vector3 hitBoxCorner000 = hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(hitBox.centerPosition));

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner111, hitBoxCorner110);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner111, hitBoxCorner101);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner111, hitBoxCorner011);

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner010, hitBoxCorner011);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner010, hitBoxCorner000);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner010, hitBoxCorner110);

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner001, hitBoxCorner000);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner001, hitBoxCorner011);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner001, hitBoxCorner101);

        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner100, hitBoxCorner101);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner100, hitBoxCorner110);
        renderer.drawLine3d(Color.LIGHT_GRAY, hitBoxCorner100, hitBoxCorner000);
    }
}
