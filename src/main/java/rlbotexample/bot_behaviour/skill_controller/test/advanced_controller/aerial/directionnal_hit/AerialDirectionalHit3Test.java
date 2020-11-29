package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.ball_prediction.DebugCustomBallPrediction;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit3;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.aerial_hit.AerialHitSetup1;
import util.game_situation.situations.aerial_hit.AerialHitSetup2;
import util.game_situation.situations.aerial_hit.AerialHitSetup3;
import util.game_situation.situations.aerial_hit.AerialHitSetup4;
import util.game_situation.miscellaneous.RemoveResidualVelocity;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class AerialDirectionalHit3Test extends PanBot {

    private AerialDirectionalHit3 aerialDirectionalHitController;
    private TrainingPack gameSituationHandler;
    private Predictions predictions;

    public AerialDirectionalHit3Test() {
        predictions = new Predictions();
        aerialDirectionalHitController = new AerialDirectionalHit3(this);
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup1());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup2());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup3());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup4());
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situation handling
        //gameSituationHandler.update();

        // do the thing
        aerialDirectionalHitController.setBallDestination(new Vector3(0, 5120, 100));
        aerialDirectionalHitController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        aerialDirectionalHitController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        new DebugCustomBallPrediction().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
