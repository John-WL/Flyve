package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state.TakeBoostState;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class MainBehaviourState implements State {

    private BotBehaviour bot;

    private final StateMachine stateMachine;

    public MainBehaviourState(BotBehaviour bot) {
        this.bot = bot;
        State takeBoostState = new TakeBoostState(bot);
        this.stateMachine = new StateMachine(takeBoostState);
    }

    @Override
    public void exec(DataPacket input) {
        stateMachine.exec(input);
    }

    @Override
    public State next(DataPacket input) {
        if(input.ball.velocity.magnitude() >= 0.1
                || input.ball.position.flatten().magnitude() >= 0.1) {
            return this;
        }
        else {
            return new KickoffState(bot);
        }
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        //renderer.drawString3d("main behaviour", Color.YELLOW, input.car.position, 2, 2);
        stateMachine.debug(input, renderer);
    }
}
