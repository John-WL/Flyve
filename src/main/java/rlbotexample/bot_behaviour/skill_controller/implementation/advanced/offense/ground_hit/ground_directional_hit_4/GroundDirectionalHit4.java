package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit.ground_directional_hit_4;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit.ground_directional_hit_4.state.InitializationState;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Trajectory3D;
import util.state_machine.StateMachine;

public class GroundDirectionalHit4 extends SkillController {

    private final StateMachine stateMachine;
    private final InitializationState initializationState;

    public GroundDirectionalHit4(BotBehaviour bot) {
        initializationState = new InitializationState(bot);
        stateMachine = new StateMachine(initializationState);
    }

    public void setDestinationTrajectory(Trajectory3D trajectory) {
        initializationState.setDestinationTrajectory(trajectory);
    }

    public void setOrientationTrajectory(Trajectory3D trajectory) {
        initializationState.setOrientationTrajectory(trajectory);
    }

    @Override
    public void updateOutput(DataPacket input) {
        stateMachine.exec(input);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        stateMachine.debug(input, renderer);
    }
}
