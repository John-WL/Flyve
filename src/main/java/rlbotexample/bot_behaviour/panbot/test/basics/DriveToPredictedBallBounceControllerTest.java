package rlbotexample.bot_behaviour.panbot.test.basics;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DriveToPredictedBallBounceController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

public class DriveToPredictedBallBounceControllerTest extends PanBot {

    private DriveToPredictedBallBounceController driveToPredictedBallBounceController;

    public DriveToPredictedBallBounceControllerTest() {
        driveToPredictedBallBounceController = new DriveToPredictedBallBounceController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        driveToPredictedBallBounceController.setDestination(new Vector3(0, 6000 * (input.team == 0 ? 1 : -1), 0));
        driveToPredictedBallBounceController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        driveToPredictedBallBounceController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
