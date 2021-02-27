package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.recovery;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.intersect_destination.AerialIntersectDestination2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.recovery.AerialRecovery;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.miscellaneous.RemoveResidualVelocity;
import util.game_situation.situations.aerial_hit.AerialHitSetup1;
import util.game_situation.situations.aerial_hit.AerialHitSetup2;
import util.game_situation.situations.aerial_hit.AerialHitSetup3;
import util.game_situation.situations.throwing_player_in_the_air.ThrowingPlayer1;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;

public class AerialRecoveryTest extends FlyveBot {

    private AerialRecovery aerialRecoveryController;
    private TrainingPack gameSituationHandler;

    public AerialRecoveryTest() {
        aerialRecoveryController = new AerialRecovery(this);
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new ThrowingPlayer1());
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situation handling
        if(gameSituationHandler.updatingWontBreakBot(input)) {
            gameSituationHandler.update();
        }

        // do the thing
        aerialRecoveryController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        aerialRecoveryController.debug(renderer, input);
    }
}
