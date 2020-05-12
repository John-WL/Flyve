package rlbotexample.bot_behaviour.panbot.test;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.debug.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.test_controller.AerialHit;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.*;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;

public class AerialHitPredictionTest extends PanBot {

    private SkillController aerialHitController;
    private GameSituationHandler gameSituationHandler;
    private Predictions predictions;

    public AerialHitPredictionTest() {

        predictions = new Predictions();
        aerialHitController = new AerialHit(this, predictions);
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup1());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup2());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup3());
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situation handling
        //gameSituationHandler.update();

        // load the getNativeBallPrediction prediction path so we don't overuse the implementation.
        // If we use too much the core implementation, it lags and breaks, sometimes D:
        predictions.loadNativeBallPrediction();

        // do the thing
        aerialHitController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }



    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        aerialHitController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
