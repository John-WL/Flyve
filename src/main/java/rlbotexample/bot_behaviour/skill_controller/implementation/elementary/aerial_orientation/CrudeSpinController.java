package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.PitchVelocityOffsetFinder;
import rlbotexample.input.dynamic_data.aerials.RollVelocityOffsetFinder;
import rlbotexample.input.dynamic_data.aerials.YawVelocityOffsetFinder;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class CrudeSpinController extends SkillController {

    private BotBehaviour bot;
    private Vector3 desiredSpin;

    public CrudeSpinController(BotBehaviour bot) {
        this.bot = bot;
        this.desiredSpin = new Vector3();
    }

    public void setSpin(Vector3 desiredSpin) {
        this.desiredSpin = desiredSpin;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 localDesiredSpin = desiredSpin.toFrameOfReference(input.car.orientation);
        Vector3 localSpin = input.car.spin.toFrameOfReference(input.car.orientation);
        Vector3 localDeltaSpinAmount = localDesiredSpin.minus(localSpin).scaled(0.05);
        //Vector3 localDeltaSpinAmount = new Vector3();

        double rollAmount = RollVelocityOffsetFinder.compute(localDesiredSpin.x);
        double pitchAmount = PitchVelocityOffsetFinder.compute(localDesiredSpin.y);
        double yawAmount = YawVelocityOffsetFinder.compute(localDesiredSpin.z);

        output.roll(rollAmount + localDeltaSpinAmount.x);
        output.pitch(pitchAmount - localDeltaSpinAmount.y);
        output.yaw(yawAmount - localDeltaSpinAmount.z);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.GREEN, input.car.position, input.car.position.plus(desiredSpin.scaled(100)));
        renderer.drawLine3d(Color.blue, input.car.position, input.car.position.plus(input.car.spin.scaled(100)));
    }
}
