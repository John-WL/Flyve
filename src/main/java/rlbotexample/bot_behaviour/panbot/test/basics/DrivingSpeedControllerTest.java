package rlbotexample.bot_behaviour.panbot.test.basics;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.aerials.AerialDirectionalHit;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.AerialHitSetup1;
import util.game_situation.AerialHitSetup2;
import util.game_situation.AerialHitSetup3;
import util.game_situation.RemoveResidualVelocity;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import util.vector.Vector3;

public class DrivingSpeedControllerTest extends PanBot {

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
