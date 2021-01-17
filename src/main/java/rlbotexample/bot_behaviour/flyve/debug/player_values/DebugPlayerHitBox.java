package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class DebugPlayerHitBox extends FlyveBot {

    public DebugPlayerHitBox() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        ExtendedCarData playerCar = input.car;
        Vector3 playerPosition = playerCar.position;
        Vector3 playerNoseOrientation = input.car.orientation.noseVector;
        Vector3 playerRoofOrientation = input.car.orientation.roofVector;
        Vector3 playerRightOrientation = input.car.orientation.rightVector;

        Vector3 hitBoxCorner111 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.plus(playerRoofOrientation).plus(playerRightOrientation).scaled(300).plus(playerPosition));
        Vector3 hitBoxCorner110 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.plus(playerRoofOrientation).plus(playerRightOrientation.scaled(-1)).scaled(300).plus(playerPosition));
        Vector3 hitBoxCorner101 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.plus(playerRoofOrientation.scaled(-1)).plus(playerRightOrientation).scaled(300).plus(playerPosition));
        Vector3 hitBoxCorner100 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.plus(playerRoofOrientation.scaled(-1)).plus(playerRightOrientation.scaled(-1)).scaled(300).plus(playerPosition));
        Vector3 hitBoxCorner011 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.scaled(-1).plus(playerRoofOrientation).plus(playerRightOrientation).scaled(300).plus(playerPosition));
        Vector3 hitBoxCorner010 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.scaled(-1).plus(playerRoofOrientation).plus(playerRightOrientation.scaled(-1)).scaled(300).plus(playerPosition));
        Vector3 hitBoxCorner001 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.scaled(-1).plus(playerRoofOrientation.scaled(-1)).plus(playerRightOrientation).scaled(300).plus(playerPosition));
        Vector3 hitBoxCorner000 = playerCar.hitBox.projectPointOnSurface(playerNoseOrientation.scaled(-1).plus(playerRoofOrientation.scaled(-1)).plus(playerRightOrientation.scaled(-1)).scaled(300).plus(playerPosition));

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
