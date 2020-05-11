package rlbotexample.bot_behaviour.panbot.test;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.panbot.debug.DebugPredictedAerialHitOnBall;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.AerialDirectionalHit;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.AerialIntersectDestination;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.*;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import util.vector.Vector3;

// this implementation NEEDS at least 2 bots to work!
// else it just crashes lul
public class AerialPassingPlayTest extends PanBot {

    private AerialDirectionalHit aerialDirectionalHitControllerBot0;
    private AerialDirectionalHit aerialDirectionalHitControllerBot1;
    private AerialIntersectDestination aerialIntersectDestinationBot1;
    private GameSituationHandler gameSituationHandler;
    private Predictions predictionsForBot0;
    private Predictions predictionsForBot1;

    public AerialPassingPlayTest() {
        predictionsForBot0 = new Predictions();
        predictionsForBot1 = new Predictions();
        aerialDirectionalHitControllerBot0 = new AerialDirectionalHit(this, predictionsForBot0);
        aerialDirectionalHitControllerBot1 = new AerialDirectionalHit(this, predictionsForBot1);
        aerialIntersectDestinationBot1 = new AerialIntersectDestination(this, predictionsForBot1);
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialPassingPlaySetup1());
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situation handling
        gameSituationHandler.update();

        // do the thing
        // bot0 behaviour (starts the pass)
        if(input.playerIndex == 0) {
            // load the ball prediction path so we don't overuse the implementation.
            // If we use too much the core implementation, it lags and breaks, sometimes D:
            predictionsForBot0.loadBallPrediction();

            // pass the damn ball
            aerialDirectionalHitControllerBot0.setBallDestination(input.allCars.get(1).position);
            aerialDirectionalHitControllerBot0.updateOutput(input);
        }
        // bot1 behaviour (receives the pass and tries to score)
        else if(input.playerIndex == 1) {
            // load the ball prediction path so we don't overuse the implementation.
            // If we use too much the core implementation, it lags and breaks, sometimes D:
            predictionsForBot1.loadBallPrediction();

            // if bot0 goes for a pass
            if(input.allCars.get(0).velocity.minus(input.ball.velocity).dotProduct(input.allCars.get(0).position.minus(input.ball.position)) < 0
            && input.allCars.get(1).position.minus(input.ball.position).magnitude() > 1000) {
                aerialIntersectDestinationBot1.setDestination(new Vector3(-400, 1500, 800));
                aerialIntersectDestinationBot1.updateOutput(input);
            }
            // if bot0 did the pass
            else {
                aerialDirectionalHitControllerBot1.setBallDestination(new Vector3(0, 5500, 100));
                aerialDirectionalHitControllerBot1.updateOutput(input);
            }
        }

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        aerialDirectionalHitControllerBot0.debug(renderer, input);
        aerialDirectionalHitControllerBot1.debug(renderer, input);
        aerialIntersectDestinationBot1.debug(renderer, input);
        new DebugPredictedAerialHitOnBall().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
