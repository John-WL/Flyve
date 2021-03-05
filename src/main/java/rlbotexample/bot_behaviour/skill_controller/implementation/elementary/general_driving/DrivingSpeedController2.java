package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.controls_output_utils.BaseForwardThrottleControlsOutput;
import util.game_constants.RlConstants;

// isn't warranted to work with accelerations below 100 uu/s^2 !
public class DrivingSpeedController2 extends SkillController {

    private final BotBehaviour bot;

    private final DrivingAccelerationController drivingAccelerationController;
    private double desiredSpeed;

    double carSpeed;

    public DrivingSpeedController2(BotBehaviour bot) {
        this.bot = bot;
        this.drivingAccelerationController = new DrivingAccelerationController(bot);
        this.desiredSpeed = 0;

        this.carSpeed = 0;
    }

    public void setSpeed(final double desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    @Override
    public void updateOutput(DataPacket input) {
        carSpeed = input.car.velocity.dotProduct(input.car.orientation.noseVector);

        System.out.println(input.car.velocity.magnitude());

        //bot.output().steer(1);

        drivingAccelerationController.setAcceleration((desiredSpeed - carSpeed)*80);
        drivingAccelerationController.updateOutput(input);

        bot.output().boost(false);
        if(input.car.hasWheelContact) {
            if ((desiredSpeed - carSpeed > 5)) {
                bot.output().boost(true);
            }
        }
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {

    }
}
