package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.controllers.throttle_controller.ThrottleController;
import util.math.vector.Vector3;

import java.awt.*;

public class DrivingSpeedController extends SkillController {

    private final BotBehaviour bot;
    private final PidController throttlePid = new PidController(0.01, 0, 0);
    private double speedToReach;
    private double throttleAmount;

    public DrivingSpeedController(BotBehaviour bot) {
        this.bot = bot;
        speedToReach = 1410;
        throttleAmount = 0;
    }

    public void setSpeed(final double speedToReach) {
        this.speedToReach = speedToReach;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        throttleAmount = throttlePid.process(speedToReach, input.car.velocity.dotProduct(input.car.orientation.noseVector));
        output.throttle(ThrottleController.process(throttleAmount));
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        if(throttleAmount < 0) {
            renderer.drawLine3d(Color.green, input.car.position.plus(new Vector3(0, 0, 100)).toFlatVector(), input.car.position.plus(input.car.orientation.noseVector.scaled(throttleAmount * -100)).plus(new Vector3(0, 0, 100)).toFlatVector());
        }
        else {
            renderer.drawLine3d(Color.red, input.car.position.plus(new Vector3(0, 0, 100)).toFlatVector(), input.car.position.plus(input.car.orientation.noseVector.scaled(throttleAmount * -100)).plus(new Vector3(0, 0, 100)).toFlatVector());
        }
        if(throttleAmount > 1) {
            renderer.drawCenteredRectangle3d(Color.blue, input.car.position.plus(new Vector3(0, 0, 100)).toFlatVector(), 10, 10, true);
        }
    }
}
