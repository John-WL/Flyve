package rlbotexample.bot_behaviour.panbot.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit5;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit6;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;

public class FiveHeadAerialState implements State {

    private BotBehaviour bot;

    private AerialDirectionalHit6 aerialController;

    public FiveHeadAerialState(BotBehaviour bot) {
        this.bot = bot;
        this.aerialController = new AerialDirectionalHit6(bot);
    }

    @Override
    public void exec(DataPacket input) {
        aerialController.setBallDestination(new Vector3());
        aerialController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(input.ball.velocity.minus(input.car.velocity).dotProduct(input.ball.position.minus(input.car.position)) < 0) {
            return this;
        }

        return new GoToBackPostState(bot);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("5head aerial", Color.YELLOW, input.car.position, 2, 2);
    }
}
