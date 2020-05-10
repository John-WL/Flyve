package rlbotexample.bot_behaviour.panbot.test;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.AerialDirectionalHit;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.AerialIntersectDestination;
import rlbotexample.input.dynamic_data.DataPacket;
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

    public AerialPassingPlayTest() {
        aerialDirectionalHitControllerBot0 = new AerialDirectionalHit(this);
        aerialDirectionalHitControllerBot1 = new AerialDirectionalHit(this);
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
        if(input.playerIndex == 0) {
            if(input.ball.position.y > -2000) {
                aerialDirectionalHitControllerBot0.setBallDestination(input.allCars.get(1).position);
                aerialDirectionalHitControllerBot0.updateOutput(input);
            }
            else {
                aerialDirectionalHitControllerBot0.setBallDestination(new Vector3(0, 5500, 100));
                aerialDirectionalHitControllerBot0.updateOutput(input);
            }
        }
        else if(input.playerIndex == 1) {
            // if bot1 goes for a pass
            if(input.allCars.get(0).velocity.minus(input.ball.velocity).dotProduct(input.allCars.get(0).position.minus(input.ball.position)) < 0) {
                aerialIntersectDestinationBot1.setDestination(new Vector3(1000, 2000, 800));
                aerialIntersectDestinationBot1.updateOutput(input);
            }
            // if bot1 did the pass
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
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
