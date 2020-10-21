package rlbotexample.bot_behaviour.panbot.test.aerial;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.ball_prediction.DebugPredictedAerialHitOnBall;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.intersect_destination.AerialIntersectDestination;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.setup.AerialSetupController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.*;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import util.math.vector.Vector3;

// this implementation NEEDS at least 2 bots to work!
// else it just crashes lul
public class AerialPassingPlayTest extends PanBot {

    private AerialSetupController aerialDirectionalHitControllerBot0;
    private AerialSetupController aerialDirectionalHitControllerBot1;
    private AerialIntersectDestination aerialIntersectDestinationBot1;
    private GameSituationHandler gameSituationHandler;

    public AerialPassingPlayTest() {
        aerialDirectionalHitControllerBot0 = new AerialSetupController(this);
        aerialDirectionalHitControllerBot1 = new AerialSetupController(this);
        aerialIntersectDestinationBot1 = new AerialIntersectDestination(this);
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
            // load the getNativeBallPrediction prediction path so we don't overuse the implementation.
            // If we use too much the core implementation, it lags and breaks, sometimes D:
            //predictionsForBot0.loadNativeBallPrediction();

            // pass the damn getNativeBallPrediction
            /*if(input.allCars.get(0).velocity.minus(input.ball.velocity).dotProduct(input.allCars.get(0).position.minus(input.ball.position)) < 0) {
                aerialDirectionalHitControllerBot0.setBallDestination(new Vector3(-1400, 1000, 10000));
                aerialDirectionalHitControllerBot0.updateOutput(input);
            }*/
            if(input.ball.position.y > -1000) {
                aerialDirectionalHitControllerBot0.setBallDestination(new Vector3(500, 2000, 1000));
                aerialDirectionalHitControllerBot0.updateOutput(input);
            }
            else {
                aerialDirectionalHitControllerBot0.setBallDestination(new Vector3(0, 5500, 100));
                aerialDirectionalHitControllerBot0.updateOutput(input);
            }
        }
        // bot1 behaviour (receives the pass and tries to score)
        else if(input.playerIndex == 1) {
            // load the getNativeBallPrediction prediction path so we don't overuse the implementation.
            // If we use too much the core implementation, it lags and breaks, sometimes D:
            //predictionsForBot1.loadNativeBallPrediction();

            // if bot0 goes for a pass
            /*if(input.allCars.get(0).velocity.minus(input.ball.velocity).dotProduct(input.allCars.get(0).position.minus(input.ball.position)) < 0
            && input.allCars.get(1).position.minus(input.ball.position).magnitude() > 1000) {
                aerialIntersectDestinationBot1.setDestination(new Vector3(-400, 1500, 800));
                aerialIntersectDestinationBot1.updateOutput(input);
            }
            // if bot0 did the pass
            else {
                aerialDirectionalHitControllerBot1.setBallDestination(new Vector3(0, 5500, 100));
                aerialDirectionalHitControllerBot1.updateOutput(input);
            }*/
            // if bot1 goes for a pass
            //if(input.allCars.get(0).velocity.minus(input.ball.velocity).dotProduct(input.allCars.get(0).position.minus(input.ball.position)) < 0) {
                //aerialIntersectDestinationBot1.setDestination(new Vector3(500, 2000, 800));
                //aerialIntersectDestinationBot1.updateOutput(input);
            //}
            // if bot1 did the pass
            //else {
                aerialDirectionalHitControllerBot1.setBallDestination(new Vector3(0, 5500, 1000));
                aerialDirectionalHitControllerBot1.updateOutput(input);
            //}
        }

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        if(input.playerIndex == 0) {
            new DebugPredictedAerialHitOnBall().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
            aerialDirectionalHitControllerBot0.debug(renderer, input);
        }
        else if(input.playerIndex == 1) {
            new DebugPredictedAerialHitOnBall().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
            aerialDirectionalHitControllerBot1.debug(renderer, input);
            //aerialIntersectDestinationBot1.debug(renderer, input);
        }

    }
}
