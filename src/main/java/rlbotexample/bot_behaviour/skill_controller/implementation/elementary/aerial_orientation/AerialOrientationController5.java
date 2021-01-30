package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation;

import rlbot.render.Renderer;
import rlbotexample.SampleBot;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialOrientationController5 extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController2 orientationController;
    private Vector3 noseOrientation;
    private Vector3 rollOrientation;
    private Vector3 previousNoseOrientation;
    private Vector3 previousRollOrientation;
    private Vector3 correctedNoseDestination;
    private Vector3 correctedRollDestination;

    public AerialOrientationController5(BotBehaviour bot) {
        this.bot = bot;
        this.orientationController = new AerialOrientationController2(bot);
        this.noseOrientation = new Vector3();
        this.rollOrientation = new Vector3();
        this.previousNoseOrientation = new Vector3();
        this.previousRollOrientation = new Vector3();
        this.correctedNoseDestination = new Vector3();
        this.correctedRollDestination = new Vector3();
    }

    public void setNoseOrientation(Vector3 noseOrientation) {
        this.noseOrientation = noseOrientation.normalized();
    }

    public void setRollOrientation(Vector3 rollOrientation) {
        this.rollOrientation = rollOrientation.normalized();
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 noseDestinationAngularVelocity = previousNoseOrientation.findRotator(noseOrientation).scaled(RlConstants.BOT_REFRESH_RATE);
        Vector3 rollDestinationAngularVelocity = previousRollOrientation.findRotator(rollOrientation).scaled(RlConstants.BOT_REFRESH_RATE);
        previousNoseOrientation = noseOrientation;
        previousRollOrientation = rollOrientation;

        correctedNoseDestination = noseOrientation.rotate(noseDestinationAngularVelocity.scaled(0.33));
        correctedRollDestination = rollOrientation.rotate(rollDestinationAngularVelocity.scaled(0));

        orientationController.setOrientationDestination(input.car.position.plus(correctedNoseDestination));
        orientationController.setRollOrientation(input.car.position.plus(correctedRollDestination));
        orientationController.updateOutput(input);

    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.magenta, correctedNoseDestination.scaled(400).plus(input.car.position), input.car.position);

        renderer.drawLine3d(Color.GREEN, noseOrientation.scaled(300).plus(input.car.position), input.car.position);
        renderer.drawLine3d(Color.blue, rollOrientation.scaled(300).plus(input.car.position), input.car.position);


    }
}
