package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.debug.ball_prediction.DebugCustomBallPrediction;
import rlbotexample.bot_behaviour.flyve.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.WallToAirDribbleController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.miscellaneous.RemoveResidualVelocity;
import util.game_situation.situations.aerial_hit.AerialHitSetup1;
import util.game_situation.situations.aerial_hit.AerialHitSetup2;
import util.game_situation.situations.aerial_hit.AerialHitSetup3;
import util.game_situation.situations.aerial_hit.AerialHitSetup4;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class WallToAirDribbleTest extends FlyveBot {

    public WallToAirDribbleController wallToAirDribbleController;

    public WallToAirDribbleTest() {
        this.wallToAirDribbleController = new WallToAirDribbleController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        wallToAirDribbleController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
