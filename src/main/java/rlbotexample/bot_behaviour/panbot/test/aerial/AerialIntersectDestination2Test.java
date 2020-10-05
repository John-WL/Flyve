package rlbotexample.bot_behaviour.panbot.test.aerial;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.aerials.AerialIntersectDestination;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.aerials.AerialIntersectDestination2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.RlUtils;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.AerialHitSetup1;
import util.game_situation.AerialHitSetup2;
import util.game_situation.AerialHitSetup3;
import util.game_situation.RemoveResidualVelocity;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import util.vector.Vector3;

public class AerialIntersectDestination2Test extends PanBot {

    private AerialIntersectDestination2 aerialIntersectDestinationController;
    private GameSituationHandler gameSituationHandler;

    public AerialIntersectDestination2Test() {
        aerialIntersectDestinationController = new AerialIntersectDestination2(this);
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

        // do the thing
        //aerialIntersectDestinationController.setDestination(input.allCars.get(1-input.car.playerIndex).position);
        Vector3 playerDistanceFromBall = input.ball.position.minus(input.car.position);
        Vector3 playerSpeedFromBall = input.ball.velocity.minus(input.car.velocity);
        double timeToReach = RlUtils.timeToReachAerialDestination(input, playerDistanceFromBall, playerSpeedFromBall)*1.05;
        aerialIntersectDestinationController.setDestination(input.allCars.get(1-input.playerIndex).position);
        aerialIntersectDestinationController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        aerialIntersectDestinationController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
