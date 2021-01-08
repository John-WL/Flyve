package rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.FlyveBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class GroundOrientationControllerTest extends FlyveBot {

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    public GroundOrientationControllerTest() {
        drivingSpeedController = new DrivingSpeedController(this);
        groundOrientationController = new GroundOrientationController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        double timeElapsed = System.currentTimeMillis()/1000.0;
        // do the thing
        drivingSpeedController.updateOutput(input);
        //groundOrientationController.setDestination(new Vector3(0, Math.sin(timeElapsed/5)*1000, 50));
        groundOrientationController.setDestination(input.ball.position);
        groundOrientationController.updateOutput(input);

        double actualTurningRate = input.car.spin.dotProduct(input.car.orientation.roofVector);
        //System.out.println((timeElapsed%2 * 2)-1 - actualTurningRate);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        groundOrientationController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
