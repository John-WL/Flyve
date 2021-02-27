package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state.ground_dribble_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;

import java.awt.*;

public class ShwoooPakState implements State {

    private BotBehaviour bot;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    public ShwoooPakState(BotBehaviour bot) {
        this.bot = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
    }

    @Override
    public void exec(DataPacket input) {
        drivingSpeedController.setSpeed(2300);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(input.ball.position);
        groundOrientationController.updateOutput(input);

        bot.output().boost(false);
    }

    @Override
    public State next(DataPacket input) {
        if(input.ball.velocity.minus(input.car.velocity).dotProduct(input.ball.position.minus(input.car.position)) < 0) {
            return this;
        }

        return new AlignCarForShwoooPakState(bot);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("\"shwooo pak\"", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
    }
}
