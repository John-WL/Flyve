package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.KickoffState;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class Normal1sV4 extends FlyveBot {

    private final StateMachine stateMachine;

    public Normal1sV4() {
        State initialState = new KickoffState(this);
        stateMachine = new StateMachine(initialState);
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        stateMachine.exec(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        stateMachine.debug(input, renderer);
    }
}
