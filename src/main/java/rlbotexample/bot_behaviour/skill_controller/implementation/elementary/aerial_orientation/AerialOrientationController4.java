package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialOrientationController4 extends SkillController {

    private BotBehaviour bot;
    private CrudeSpinController spinController;
    private PidController pidControl = new PidController(10, 0, 3);
    private Vector3 noseOrientation;
    private Vector3 rollOrientation;


    public AerialOrientationController4(BotBehaviour bot) {
        this.bot = bot;
        this.spinController = new CrudeSpinController(bot);
        this.noseOrientation = new Vector3();
        this.rollOrientation = new Vector3();

    }

    public void setNoseOrientation(Vector3 noseOrientation) {
        this.noseOrientation = noseOrientation;
    }

    public void setRollOrientation(Vector3 rollOrientation) {
        this.rollOrientation = rollOrientation;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 noseRotator = input.car.orientation.noseVector.crossProduct(noseOrientation).scaledToMagnitude(input.car.orientation.noseVector.angle(noseOrientation));
        Vector3 roofRotator = input.car.orientation.roofVector.rotate(noseRotator).crossProduct(rollOrientation).scaledToMagnitude(input.car.orientation.roofVector.rotate(noseRotator).angle(rollOrientation));
        Vector3 resultingRotator = noseRotator.plus(roofRotator.scaled(Math.max(0, input.car.orientation.noseVector.dotProduct(noseOrientation.normalized())/2)));
        double rotatorAngle = pidControl.process(resultingRotator.magnitude(), 0);
        spinController.setSpin(resultingRotator.scaledToMagnitude(rotatorAngle));
        spinController.updateOutput(input);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        //renderer.drawLine3d(Color.GREEN, desiredSpin.minus(input.car.spin.toFrameOfReference(input.car.orientation)).scaled(10).plus(new Vector3(0, 0, 700)), new Vector3(0, 0, 700));
        renderer.drawLine3d(Color.GREEN, noseOrientation.scaled(300).plus(input.car.position).toFlatVector(), input.car.position.toFlatVector());
        renderer.drawLine3d(Color.blue, rollOrientation.scaled(300).plus(input.car.position).toFlatVector(), input.car.position.toFlatVector());
    }
}
