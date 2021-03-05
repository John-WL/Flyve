package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.directionnal_hit.parabola.aerial_directional_hit_7;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.directionnal_hit.parabola.aerial_directional_hit_7.states.DriveTowardAerialState;
import rlbotexample.input.dynamic_data.DataPacket;
import util.renderers.ShapeRenderer;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class AerialDirectionalHit7 extends SkillController {

    private BotBehaviour bot;
    private final StateMachine aerialMachine;

    public AerialDirectionalHit7(BotBehaviour bot) {
        this.bot = bot;
        State driveTowardAerialState = new DriveTowardAerialState(bot);
        this.aerialMachine = new StateMachine(driveTowardAerialState);
    }

    @Override
    public void updateOutput(DataPacket input) {
        aerialMachine.exec(input);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        aerialMachine.debug(input, renderer);
    }
}
