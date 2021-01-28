package rlbotexample.bot_behaviour.flyve.implementation.state_machine_freestyle;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.KickoffState;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class DribbleThenJumpAndAerialBot extends FlyveBot {

    private final StateMachine stateMachine;

    public DribbleThenJumpAndAerialBot() {
        State initialState = new DribbleState(this);
        stateMachine = new StateMachine(initialState);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        stateMachine.exec(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        stateMachine.debug(input, renderer);
    }
}
