package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SpeedFlip;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;

public class KickoffState implements State {

    private BotBehaviour bot;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;
    private JumpController jumpController;

    public KickoffState(BotBehaviour bot) {
        this.bot = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.jumpController = new JumpController(bot);
    }

    @Override
    public void exec(DataPacket input) {
        drivingSpeedController.setSpeed(2300);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(input.ball.position);
        if(input.car.position.minus(input.ball.position).magnitude() > 2000) {
            groundOrientationController.setDestination(input.ball.position.plus(new Vector3(1000, 0, 0)));
        }
        groundOrientationController.updateOutput(input);

        bot.output().boost(true);

        jumpController.setJumpDestination(input.ball.position);
        jumpController.setFirstJumpType(new ShortJump(), input);
        jumpController.setSecondJumpType(new SpeedFlip(), input);
        if(input.car.velocity.magnitude() <= 800) {
            return;
        }
        if(input.car.velocity.magnitude() > 2000
            && input.car.position.minus(input.ball.position).magnitude() > 700) {
            return;
        }
        jumpController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(input.ball.velocity.magnitude() < 0.1
                && input.ball.position.flatten().magnitude() < 0.1) {
            return this;
        }
        else {
            return new MainBehaviourState(bot);
        }
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("kickoff", Color.YELLOW, input.car.position, 2, 2);
    }
}
