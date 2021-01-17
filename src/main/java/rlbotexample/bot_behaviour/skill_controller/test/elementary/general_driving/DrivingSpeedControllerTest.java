package rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class DrivingSpeedControllerTest extends FlyveBot {

    private DrivingSpeedController drivingSpeedController;

    public DrivingSpeedControllerTest() {
        drivingSpeedController = new DrivingSpeedController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        drivingSpeedController.setSpeed(1410);
        drivingSpeedController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        drivingSpeedController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
