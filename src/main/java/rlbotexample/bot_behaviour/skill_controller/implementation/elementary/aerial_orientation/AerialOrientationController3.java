package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.Orientation;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialOrientationController3 extends SkillController {

    private BotBehaviour bot;
    private Orientation desiredOrientation;
    private Vector3 desiredSpin;

    private PidController angularMomentumPid;

    public AerialOrientationController3(BotBehaviour bot) {
        this.bot = bot;
        this.desiredOrientation = new Orientation();
        this.desiredSpin = new Vector3();

        this.angularMomentumPid = new PidController(1, 0, 0.7);
    }

    public void setNoseOrientation(Vector3 desiredNoseOrientation) {
        this.desiredOrientation = new Orientation(desiredNoseOrientation, desiredOrientation.roof);
    }

    public void setRollOrientation(Vector3 desiredRollOrientation) {
        this.desiredOrientation = new Orientation(desiredOrientation.nose, desiredRollOrientation);
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();

        Vector3 rotator = input.car.orientation.findRotatorToRotateTo(desiredOrientation);
        rotator = rotator.toFrameOfReference(input.car.orientation);

        rotator = rotator.scaledToMagnitude(-angularMomentumPid.process(0, rotator.magnitude()));

        output.roll(rotator.x);
        output.pitch(-rotator.y);
        output.yaw(-rotator.z);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.GREEN, desiredSpin.minus(input.car.spin.toFrameOfReference(input.car.orientation)).scaled(10).plus(new Vector3(0, 0, 700)), new Vector3(0, 0, 700));
    }
}
