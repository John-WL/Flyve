package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialOrientationController2 extends SkillController {

    private BotBehaviour bot;
    private Vector3 orientationDestination;
    private Vector3 rollOrientation;
    private Vector3 desiredSpin;

    private PidController pitchVelocityPid;
    private PidController yawVelocityPid;
    private PidController rollVelocityPid;

    public AerialOrientationController2(BotBehaviour bot) {
        this.bot = bot;
        this.orientationDestination = new Vector3();
        this.rollOrientation = new Vector3();
        this.desiredSpin = new Vector3();

        /*this.pitchVelocityPid = new PidController(3, 0, 0.3);
        this.yawVelocityPid = new PidController(3, 0, 0.2);
        this.rollVelocityPid = new PidController(0.6, 0, 0.06);*/

        this.pitchVelocityPid = new PidController(1.5, 0, 0.15);
        this.yawVelocityPid = new PidController(1.5, 0, 0.1);
        this.rollVelocityPid = new PidController(0.3, 0, 0.03);
    }

    public void setOrientationDestination(Vector3 globalDestination) {
        orientationDestination = globalDestination;
    }

    public void setRollOrientation(Vector3 rollOrientation) {
        this.rollOrientation = rollOrientation;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 localPlayerOrientationVector = orientationDestination.minus(input.car.position).toFrameOfReference(input.car.orientation);
        Vector3 localRollDestination = rollOrientation.minus(input.car.position).toFrameOfReference(input.car.orientation);

        desiredSpin = new Vector3(
            new Vector2(localRollDestination.z, localRollDestination.y).correctionAngle(new Vector2(1, 0))*13,
            new Vector2(localPlayerOrientationVector.x, -localPlayerOrientationVector.z).correctionAngle(new Vector2(1, 0))*4.3,
            new Vector2(localPlayerOrientationVector.x, localPlayerOrientationVector.y).correctionAngle(new Vector2(1, 0))*3
        );
        //double rollVelocityAmount = ;

        double rollAmount = rollVelocityPid.process(desiredSpin.x, -input.car.spin.toFrameOfReference(input.car.orientation).x);
        double pitchAmount = pitchVelocityPid.process(desiredSpin.y, input.car.spin.toFrameOfReference(input.car.orientation).y);
        double yawAmount = yawVelocityPid.process(desiredSpin.z, input.car.spin.toFrameOfReference(input.car.orientation).z);

        //System.out.println(desiredSpin.x);

        output.roll(rollAmount);
        output.pitch(pitchAmount);
        output.yaw(yawAmount);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.GREEN, desiredSpin.minus(input.car.spin.toFrameOfReference(input.car.orientation)).scaled(10).plus(new Vector3(0, 0, 700)), new Vector3(0, 0, 700));
    }
}
