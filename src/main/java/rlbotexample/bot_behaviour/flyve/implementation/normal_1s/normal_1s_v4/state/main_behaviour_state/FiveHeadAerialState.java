package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit6;
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
        aerialController.setBallDestination(new Vector3(0, 0, 1000));
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
        renderer.drawString3d("5head aerial", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
        aerialController.debug(renderer, input);
    }
}
