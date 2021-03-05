package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.setup.ground_to_aerial;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class GroundToAerialController extends SkillController {

    private BotBehaviour bot;
    private StateMachine stateMachine;

    public GroundToAerialController(BotBehaviour bot) {
        this.bot = bot;
        State initState = new GroundState(bot);
        this.stateMachine = new StateMachine(initState);
    }

    @Override
    public void updateOutput(DataPacket input) {
        stateMachine.exec(input);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        stateMachine.debug(input, renderer);
    }
}
