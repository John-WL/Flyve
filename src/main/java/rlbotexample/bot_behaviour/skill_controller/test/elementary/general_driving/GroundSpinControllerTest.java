package rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundSpinController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class GroundSpinControllerTest extends FlyveBot {

    private DrivingSpeedController drivingSpeedController;
    private GroundSpinController groundSpinController;

    public GroundSpinControllerTest() {
        drivingSpeedController = new DrivingSpeedController(this);
        groundSpinController = new GroundSpinController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        // do the thing
        //groundSpinController.setSpin(input.allCars.get(1-input.playerIndex).spin.z);
        groundSpinController.setSpin(-5.4);
        groundSpinController.updateOutput(input);

        drivingSpeedController.setSpeed(1200);
        drivingSpeedController.updateOutput(input);

        output().boost(true);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        groundSpinController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
