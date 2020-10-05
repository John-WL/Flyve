package rlbotexample.bot_behaviour.panbot.test.trash;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.trash.TurningRateController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class TurningRateControllerTest extends PanBot {

    private DrivingSpeedController drivingSpeedController;
    private TurningRateController turningRateController;

    public TurningRateControllerTest() {
        drivingSpeedController = new DrivingSpeedController(this);
        turningRateController = new TurningRateController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        double timeElapsed = System.currentTimeMillis()/1000.0;
        // do the thing
        drivingSpeedController.updateOutput(input);
        turningRateController.setTurningRate((((int)timeElapsed%2 * 2)-1)*2);
        turningRateController.updateOutput(input);

        double actualTurningRate = input.car.spin.dotProduct(input.car.orientation.roofVector);
        //System.out.println((timeElapsed%2 * 2)-1 - actualTurningRate);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        turningRateController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
