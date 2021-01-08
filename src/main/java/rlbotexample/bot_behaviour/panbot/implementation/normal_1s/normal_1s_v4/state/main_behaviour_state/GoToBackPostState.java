package rlbotexample.bot_behaviour.panbot.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
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

public class GoToBackPostState implements State {

    private BotBehaviour bot;

    private Vector3 unsignedBackPostPosition;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;
    private JumpController jumpController;

    public GoToBackPostState(BotBehaviour bot) {
        this.bot = bot;
        this.unsignedBackPostPosition = new Vector3();
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.jumpController = new JumpController(bot);
    }

    @Override
    public void exec(DataPacket input) {
        drivingSpeedController.setSpeed(2300);
        drivingSpeedController.updateOutput(input);

        unsignedBackPostPosition = getClosestBackPostPosition(input);

        groundOrientationController.setDestination(unsignedBackPostPosition);
        groundOrientationController.updateOutput(input);

        updateJump(input);

        bot.output().boost(input.car.velocity.magnitude() < 2290);
        if(!input.car.hasWheelContact) {
            bot.output().boost(false);
        }
        if(input.car.position.minus(unsignedBackPostPosition).magnitude() < 2000) {
            bot.output().boost(false);
        }
    }

    Vector3 getClosestBackPostPosition(DataPacket input) {
        Vector3 unsignedBackPostPosition = new Vector3(893, 4500, 0);
        Vector3 carPositionUnsignedInX = input.car.position;
        if(carPositionUnsignedInX.x < 0) {
            carPositionUnsignedInX = carPositionUnsignedInX.scaled(-1, 1, 1);
        }

        double carPostDistance = unsignedBackPostPosition.minus(carPositionUnsignedInX).magnitude();
        unsignedBackPostPosition = unsignedBackPostPosition.plus(new Vector3(carPostDistance*0.9, 0, 0));

        if(input.car.team == 0) {
            unsignedBackPostPosition = unsignedBackPostPosition.scaled(1, -1, 1);
        }
        if(input.car.position.x < 0) {
            unsignedBackPostPosition = unsignedBackPostPosition.scaled(-1, 1, 1);
        }

        return unsignedBackPostPosition;
    }

    void updateJump(DataPacket input) {
        jumpController.setJumpDestination(unsignedBackPostPosition);
        jumpController.setFirstJumpType(new ShortJump(), input);
        jumpController.setSecondJumpType(new SpeedFlip(), input);

        if(input.car.velocity.magnitude() < 800
                || input.car.velocity.magnitude() > 4000) {
            return;
        }
        if(input.car.orientation.noseVector.dotProduct(unsignedBackPostPosition.minus(input.car.position).normalized()) < 0.95) {
            return;
        }
        if(input.car.position.minus(unsignedBackPostPosition).magnitude() < 2000) {
            return;
        }
        jumpController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(input.statePrediction.ballAtTime(3).position.y >= 3000
                || input.statePrediction.ballAtTime(2).position.y >= 3000
                || input.statePrediction.ballAtTime(1).position.y >= 3000
                || input.statePrediction.ballAtTime(0).position.y >= 3000) {
            return new FiveHeadAerialState(bot);
        }
        if(input.car.position.minus(unsignedBackPostPosition).magnitude() > 100) {
            return this;
        }

        return new WaitingState(bot);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("go to back post", Color.YELLOW, input.car.position, 2, 2);
        renderer.drawLine3d(Color.blue, unsignedBackPostPosition, unsignedBackPostPosition.plus(new Vector3(0, 0, 300)));
    }
}
