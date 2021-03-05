package rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.recovery.AerialRecovery;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundSpinController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class DrivingSpeedController2Test extends FlyveBot {

    private DrivingSpeedController2 drivingSpeedController;

    private GroundSpinController groundSpinController;

    private AerialRecovery aerialRecovery;

    public DrivingSpeedController2Test() {
        drivingSpeedController = new DrivingSpeedController2(this);
        groundSpinController = new GroundSpinController(this);
        aerialRecovery = new AerialRecovery(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        drivingSpeedController.setSpeed(1800);
        drivingSpeedController.updateOutput(input);

        //groundSpinController.setSpin(-6);
        groundSpinController.setSpin(input.allCars.get(1-input.playerIndex).spin.z);
        groundSpinController.updateOutput(input);

        if(!input.car.hasWheelContact) {
            aerialRecovery.updateOutput(input);
        }

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        drivingSpeedController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
