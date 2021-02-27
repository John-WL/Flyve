package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialOrientationController6 extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController2 orientationController;
    private Vector3 noseOrientation;
    private Vector3 rollOrientation;

    public AerialOrientationController6(BotBehaviour bot) {
        this.bot = bot;
        this.orientationController = new AerialOrientationController2(bot);
        this.noseOrientation = new Vector3();
        this.rollOrientation = new Vector3();
    }

    public void setNoseOrientation(Vector3 noseOrientation) {
        this.noseOrientation = noseOrientation.normalized();
    }

    public void setRollOrientation(Vector3 rollOrientation) {
        this.rollOrientation = rollOrientation.normalized();
    }

    @Override
    public void updateOutput(DataPacket input) {
        double attitudeGain = 4;
        Vector3 desiredSpin = noseOrientation.findRotator(input.car.orientation.noseVector).scaled(attitudeGain);
        //Vector3 desiredSpin = rollOrientation.minus(input.car.orientation.roofVector).scaled(attitudeGain);

        double spinGain = 60;
        Vector3 desiredAcceleration = input.car.spin.minus(desiredSpin).scaled(spinGain);

        Vector3 angularAccelerationGain = new Vector3(1, 1, 1);
        bot.output().pitch(desiredAcceleration.y * angularAccelerationGain.y);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.GREEN, noseOrientation.scaled(300).plus(input.car.position).toFlatVector(), input.car.position.toFlatVector());
        renderer.drawLine3d(Color.blue, rollOrientation.scaled(300).plus(input.car.position).toFlatVector(), input.car.position.toFlatVector());
    }
}
