package rlbotexample.bot_behaviour.flyve.implementation.state_machine_freestyle;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit3;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit4;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit5;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit6;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.dribble.AirDribble2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;
import util.state_machine.State;

public class AerialHitState implements State {

    private final BotBehaviour bot;

    private final AirDribble2 aerialController;

    public AerialHitState(BotBehaviour bot) {
        this.bot = bot;
        this.aerialController = new AirDribble2(bot);
    }

    @Override
    public void exec(DataPacket input) {
        aerialController.setBallDestination(new Vector3(0, -5200, 1000));
        aerialController.updateOutput(input);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        aerialController.debug(renderer, input);
    }

    @Override
    public State next(DataPacket input) {
        if(input.ball.position.z < 100) {
            return new DribbleState(bot);
        }
        return this;
    }
}
