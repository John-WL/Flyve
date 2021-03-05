package rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class GroundOrientationController2Test extends FlyveBot {

    private GroundOrientationController2 groundOrientationController;
    private DrivingSpeedController drivingSpeedController;

    public GroundOrientationController2Test() {
        drivingSpeedController = new DrivingSpeedController(this);
        groundOrientationController = new GroundOrientationController2(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        drivingSpeedController.setSpeed(1200);
        drivingSpeedController.updateOutput(input);

        //groundOrientationController.setDestination(input.ball.position);
        //groundOrientationController.setDestination(input.allCars.get(1-input.playerIndex).position);
        groundOrientationController.setDestination(new Vector3());
        groundOrientationController.updateOutput(input);

        //output().boost(true);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        groundOrientationController.debug(renderer, input);
    }
}
