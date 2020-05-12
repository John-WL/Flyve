package rlbotexample.bot_behaviour.panbot.debug;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.HitBox;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

import java.awt.*;

public class DebugPlayerHitBox extends PanBot {

    public DebugPlayerHitBox() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        CarData opponentCar = input.allCars.get(1-input.playerIndex);
        Vector3 opponentPosition = opponentCar.position;
        Vector3 opponentNoseOrientation = input.allCars.get(1-input.playerIndex).orientation.noseVector;
        Vector3 opponentRoofOrientation = input.allCars.get(1-input.playerIndex).orientation.roofVector;
        Vector3 opponentRightOrientation = input.allCars.get(1-input.playerIndex).orientation.rightVector;

        Vector3 hitBoxCorner111 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(opponentPosition));
        Vector3 hitBoxCorner110 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(opponentPosition));
        Vector3 hitBoxCorner101 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(opponentPosition));
        Vector3 hitBoxCorner100 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(opponentPosition));
        Vector3 hitBoxCorner011 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation).scaled(300).plus(opponentPosition));
        Vector3 hitBoxCorner010 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(opponentPosition));
        Vector3 hitBoxCorner001 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation).scaled(300).plus(opponentPosition));
        Vector3 hitBoxCorner000 = opponentCar.hitBox.projectPointOnSurface(opponentNoseOrientation.scaled(-1).plus(opponentRoofOrientation.scaled(-1)).plus(opponentRightOrientation.scaled(-1)).scaled(300).plus(opponentPosition));

        renderer.drawLine3d(Color.yellow, hitBoxCorner111, hitBoxCorner110);
        renderer.drawLine3d(Color.yellow, hitBoxCorner111, hitBoxCorner101);
        renderer.drawLine3d(Color.yellow, hitBoxCorner111, hitBoxCorner011);

        renderer.drawLine3d(Color.yellow, hitBoxCorner010, hitBoxCorner011);
        renderer.drawLine3d(Color.yellow, hitBoxCorner010, hitBoxCorner000);
        renderer.drawLine3d(Color.yellow, hitBoxCorner010, hitBoxCorner110);

        renderer.drawLine3d(Color.yellow, hitBoxCorner001, hitBoxCorner000);
        renderer.drawLine3d(Color.yellow, hitBoxCorner001, hitBoxCorner011);
        renderer.drawLine3d(Color.yellow, hitBoxCorner001, hitBoxCorner101);

        renderer.drawLine3d(Color.yellow, hitBoxCorner100, hitBoxCorner101);
        renderer.drawLine3d(Color.yellow, hitBoxCorner100, hitBoxCorner110);
        renderer.drawLine3d(Color.yellow, hitBoxCorner100, hitBoxCorner000);
    }
}
