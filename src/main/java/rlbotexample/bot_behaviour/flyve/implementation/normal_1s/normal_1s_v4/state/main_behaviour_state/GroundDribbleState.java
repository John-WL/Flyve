package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state.ground_dribble_state.AlignCarForShwoooPakState;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class GroundDribbleState implements State {

    private BotBehaviour bot;

    private StateMachine stateMachine;

    public GroundDribbleState(BotBehaviour bot) {
        this.bot = bot;
        State alignCarTurnRadiusWithBallTrajectoryState = new AlignCarForShwoooPakState(bot);
        this.stateMachine = new StateMachine(alignCarTurnRadiusWithBallTrajectoryState);
    }

    @Override
    public void exec(DataPacket input) {
        stateMachine.exec(input);
    }

    @Override
    public State next(DataPacket input) {
        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        //renderer.drawString3d("ground dribble", Color.YELLOW, input.car.position, 2, 2);
        stateMachine.debug(input, renderer);
    }
}
