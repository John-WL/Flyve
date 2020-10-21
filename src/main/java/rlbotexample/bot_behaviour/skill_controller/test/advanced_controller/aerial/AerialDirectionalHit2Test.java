package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.ball_prediction.DebugCustomBallPrediction;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.AerialDirectionalHit2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.*;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import util.math.vector.Vector3;

public class AerialDirectionalHit2Test extends PanBot {

    private AerialDirectionalHit2 aerialDirectionalHitController;
    private GameSituationHandler gameSituationHandler;
    private Predictions predictions;

    public AerialDirectionalHit2Test() {
        predictions = new Predictions();
        aerialDirectionalHitController = new AerialDirectionalHit2(this);
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
        aerialDirectionalHitController.setBallDestination(new Vector3(0, 5120, 10000));
        aerialDirectionalHitController.updateOutput(input);

        output().jump(input.car.hasWheelContact);

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
