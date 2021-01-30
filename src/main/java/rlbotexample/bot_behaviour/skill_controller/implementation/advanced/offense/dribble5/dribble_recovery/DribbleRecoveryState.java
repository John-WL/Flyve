package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.dribble_recovery;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;

public class DribbleRecoveryState implements State {


    public DribbleRecoveryState(BotBehaviour bot) {

    }

    @Override
    public void exec(DataPacket input) {

    }

    @Override
    public State next(DataPacket input) {
        if(ballBouncingTooMuchToDribble(input)) {
            return this;
        }

        // no lol
        return this;
    }

    private boolean ballBouncingTooMuchToDribble(DataPacket input) {
        return input.ball.position.z > 300
                || Math.abs(input.ball.velocity.z) > 300;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {

    }
}
