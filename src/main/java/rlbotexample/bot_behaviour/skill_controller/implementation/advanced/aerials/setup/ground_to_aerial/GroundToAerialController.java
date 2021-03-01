package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.setup.ground_to_aerial;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
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
