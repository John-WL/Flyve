package rlbotexample.bot_behaviour.panbot.test.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.path.test_paths.RandomGroundPath1;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense.Dribble2;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DriveToPredictedBallBounceController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

import java.nio.file.Path;

public class Dribble2Test extends PanBot {

    private Dribble2 dribbleController;
    private RandomGroundPath1 randomGroundPath1;
    private CarDestination carDestination;

    public Dribble2Test() {
        dribbleController = new Dribble2(this);
        carDestination = new CarDestination();
        carDestination.setDesiredSpeed(1200);
        randomGroundPath1 = new RandomGroundPath1(carDestination);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        carDestination.advanceOneStep(input);
        dribbleController.setBallDestination(carDestination.getThrottleDestination());
        dribbleController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        dribbleController.debug(renderer, input);

    }
}
