package rlbotexample.bot_behaviour.panbot.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SpeedFlip;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;

public class WaitingState implements State {

    private BotBehaviour bot;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    public WaitingState(BotBehaviour bot) {
        this.bot = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
    }

    @Override
    public void exec(DataPacket input) {
        drivingSpeedController.setSpeed(0);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(input.car.position.plus(input.car.orientation.noseVector.scaled(300)));
        groundOrientationController.updateOutput(input);

        bot.output().boost(false);
    }

    @Override
    public State next(DataPacket input) {
        if(input.statePrediction.ballAtTime(3).position.y < 3000
                && input.statePrediction.ballAtTime(2).position.y < 3000
                && input.statePrediction.ballAtTime(1).position.y < 3000
                && input.statePrediction.ballAtTime(0).position.y < 3000) {
            return this;
        }

        return new GroundDribbleState(bot);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("wait", Color.YELLOW, input.car.position, 2, 2);
    }
}
