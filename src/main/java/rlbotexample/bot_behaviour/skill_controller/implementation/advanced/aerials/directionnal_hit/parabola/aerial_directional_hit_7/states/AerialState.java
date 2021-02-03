package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.aerial_directional_hit_7.states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;

public class AerialState implements State {

    private final BotBehaviour bot;

    public AerialState(BotBehaviour bot) {
        this.bot = bot;
    }

    @Override
    public void exec(DataPacket input) {

    }

    @Override
    public State next(DataPacket input) {
        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {

    }
}
