package rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.AccelerationFunctionApproximator;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class AccelerationFunctionApproximatorTest extends PanBot {

    private AccelerationFunctionApproximator accelerationFunctionApproximator;

    public AccelerationFunctionApproximatorTest() {
        accelerationFunctionApproximator = new AccelerationFunctionApproximator(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        accelerationFunctionApproximator.setSpeed(-1000);
        accelerationFunctionApproximator.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        accelerationFunctionApproximator.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
