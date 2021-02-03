package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.aerial_directional_hit_7;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.aerial_directional_hit_7.states.DriveTowardAerialState;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.car.HitBox;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.output.BotOutput;
import util.controllers.BoostController;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.state_machine.State;
import util.state_machine.StateMachine;

import java.awt.*;

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
