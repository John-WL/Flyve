package rlbotexample.bot_behaviour.flyve.implementation.memebots;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class SuperSmashRocket extends FlyveBot {

    private Vector3 forceDirection = new Vector3();
    private boolean previousHasJustUsedSecondJump = false;

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        BallStateSetter.handleBallState(input);

        for(ExtendedCarData car: input.allCars) {
            if(previousHasJustUsedSecondJump && car.playerIndex == 1) {
                forceDirection = car.spin.scaled(-1).crossProduct(car.orientation.roofVector);
                forceDirection = forceDirection.scaledToMagnitude(4000);
                System.out.println("Aya!");
                CarStateSetter.addImpulse(input.allCars.get(1-car.playerIndex), forceDirection.scaled(-1, 1, 1));
            }
        }

        previousHasJustUsedSecondJump = input.allCars.get(1-input.playerIndex).hasJustUsedSecondJump;

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        renderer.drawLine3d(Color.GREEN, forceDirection.plus(new Vector3(0, 0, 300)), new Vector3(0, 0, 300));
        renderer.drawLine3d(Color.red, input.allCars.get(1-input.playerIndex).orientation.roofVector.scaled(300).plus(new Vector3(0, 0, 300)), new Vector3(0, 0, 300));
        renderer.drawLine3d(Color.cyan, input.allCars.get(1-input.playerIndex).spin.scaled(-1).scaledToMagnitude(300).plus(new Vector3(0, 0, 300)), new Vector3(0, 0, 300));
    }
}
