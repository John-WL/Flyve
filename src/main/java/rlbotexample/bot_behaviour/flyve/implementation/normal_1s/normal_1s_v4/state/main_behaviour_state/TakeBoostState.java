package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SpeedFlip;
import rlbotexample.input.boost.BoostManager;
import rlbotexample.input.boost.BoostPad;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;
import java.util.List;

public class TakeBoostState implements State {

    private BotBehaviour bot;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;
    private JumpController jumpController;

    public TakeBoostState(BotBehaviour bot) {
        this.bot = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.jumpController = new JumpController(bot);
    }

    @Override
    public void exec(DataPacket input) {
        drivingSpeedController.setSpeed(2300);
        drivingSpeedController.updateOutput(input);

        Vector3 nearestSideBoostPosition = getClosestBigBoostPosition(input);

        groundOrientationController.setDestination(nearestSideBoostPosition);
        groundOrientationController.updateOutput(input);

        bot.output().boost(true);

        jumpController.setJumpDestination(nearestSideBoostPosition);
        jumpController.setFirstJumpType(new ShortJump(), input);
        jumpController.setSecondJumpType(new SpeedFlip(), input);
        //jumpController.updateOutput(input);
    }

    Vector3 getClosestBigBoostPosition(DataPacket input) {
        List<BoostPad> boosts = BoostManager.getFullBoosts();
        BoostPad closest = boosts.get(0);

        for(BoostPad boostPad: boosts) {
            if(boostPad.getLocation().minus(input.car.position).magnitude() < closest.getLocation().minus(input.car.position).magnitude()) {
                if(boostPad.isActive()) {
                    closest = boostPad;
                }
            }
        }

        return closest.getLocation();
    }

    @Override
    public State next(DataPacket input) {
        if(input.car.boost <= 99) {
            return this;
        }

        // change this to another state
        return new GoToBackPostState(bot);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("get boost", Color.YELLOW, input.car.position, 2, 2);
    }
}
