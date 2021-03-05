package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.controls_output_utils.BaseForwardThrottleControlsOutput;
import util.game_constants.RlConstants;

// isn't warranted to work with accelerations below 100 uu/s^2 !
public class DrivingAccelerationController extends SkillController {

    private final BotBehaviour bot;

    private final BaseForwardThrottleControlsOutput baseForwardThrottleControlsOutput;
    private double desiredAcceleration;

    double carSpeed;
    double averageCarSpeed;
    double previousCarSpeed;

    public DrivingAccelerationController(BotBehaviour bot) {
        this.bot = bot;
        this.baseForwardThrottleControlsOutput = new BaseForwardThrottleControlsOutput();
        this.desiredAcceleration = 0;

        this.carSpeed = 0;
        this.previousCarSpeed = 0;
        this.averageCarSpeed = 0;
    }

    public void setAcceleration(final double desiredAcceleration) {
        this.desiredAcceleration = desiredAcceleration;
    }

    @Override
    public void updateOutput(DataPacket input) {
        previousCarSpeed = carSpeed;
        carSpeed = input.car.velocity.dotProduct(input.car.orientation.noseVector);
        averageCarSpeed = averageCarSpeed * 0.7 + carSpeed * 0.3;
        double carSpin = input.car.spin.toFrameOfReference(input.car.orientation).z;
        double baseControls = baseForwardThrottleControlsOutput.apply(desiredAcceleration, averageCarSpeed, carSpin);

        //System.out.println((averageCarSpeed - previousCarSpeed) * RlConstants.BOT_REFRESH_RATE);
        //System.out.println(averageCarSpeed);

        if(input.car.hasWheelContact) {
            bot.output().throttle(baseControls);
            //System.out.println(baseControls);

            /*bot.output().boost(false);
            if(Math.abs(baseControls) > 1.8) {
                bot.output().boost(true);
            }*/
        }
        else {
            bot.output().throttle(0);
            /*bot.output().boost(false);*/
        }
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {

    }
}
