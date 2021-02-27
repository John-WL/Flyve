package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.game_constants.RlConstants;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

import java.awt.*;

public class AngularAccelerationController extends SkillController {

    private BotBehaviour bot;
    private Vector3 previousSpin;
    private Vector3 desiredAngularAcceleration;
    private Vector3 localAngularAcceleration;
    private Vector3 desiredLocalAngularAcceleration;

    private PidController rollAccelerationPid = new PidController(0.025, 0, 0);
    private PidController pitchAccelerationPid = new PidController(0.0, 0, 0);
    private PidController yawAccelerationPid = new PidController(0.0, 0, 0);

    public AngularAccelerationController(BotBehaviour bot) {
        this.bot = bot;
        this.previousSpin = new Vector3();
        this.desiredAngularAcceleration = new Vector3();
        this.localAngularAcceleration = new Vector3();
        this.desiredLocalAngularAcceleration = new Vector3();
    }

    public void setAngularAcceleration(Vector3 desiredAngularAcceleration) {
        this.desiredAngularAcceleration = desiredAngularAcceleration;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        localAngularAcceleration = input.car.spin.minus(previousSpin).scaled(RlConstants.BOT_REFRESH_RATE);
        previousSpin = input.car.spin;
        desiredLocalAngularAcceleration = desiredAngularAcceleration.toFrameOfReference(input.car.orientation);

        double rollAmount = rollAccelerationPid.process(localAngularAcceleration.x, desiredLocalAngularAcceleration.x);
        double pitchAmount = -pitchAccelerationPid.process(localAngularAcceleration.y, desiredLocalAngularAcceleration.y);
        double yawAmount = yawAccelerationPid.process(localAngularAcceleration.z, desiredLocalAngularAcceleration.z);

        System.out.println(localAngularAcceleration.x - desiredLocalAngularAcceleration.x);

        output.roll(rollAmount);
        output.pitch(pitchAmount);
        output.yaw(yawAmount);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.GREEN, input.car.position.toFlatVector(), input.car.position.plus(desiredAngularAcceleration.scaled(100)).toFlatVector());
        renderer.drawLine3d(Color.magenta, input.car.position.toFlatVector(), input.car.position.plus(desiredLocalAngularAcceleration.scaled(100)).toFlatVector());
        renderer.drawLine3d(Color.blue, input.car.position.toFlatVector(), input.car.position.plus(localAngularAcceleration.scaled(100)).toFlatVector());
    }
}
