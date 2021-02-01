package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.GroundTrajectoryFinder;
import rlbotexample.input.dynamic_data.ground.MaxTurnRadiusFinder;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.math.vector.Ray3;
import util.renderers.ShapeRenderer;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.shapes.Circle3D;

import java.awt.*;

public class GroundOrientationController extends SkillController {

    final private BotBehaviour bot;
    final PidController turningRatePid = new PidController(5, 0, 1);
    double turningRate;
    Vector3 destination;

    public GroundOrientationController(BotBehaviour bot) {
        this.bot = bot;
        this.turningRate = 0;
        this.destination = new Vector3();
    }

    public void setDestination(final Vector3 destinationToFace) {
        this.destination = destinationToFace;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 localDestination = destination.minus(input.car.position).toFrameOfReference(input.car.orientation.noseVector, input.car.orientation.roofVector);
        turningRate = turningRatePid.process(localDestination.flatten().correctionAngle(new Vector2(1, 0)), 0);
        output.steer(turningRate);
        boolean isDrifting;
        if(turningRate > 0) {
            Circle3D rightTurn = GroundTrajectoryFinder.getRightTurnCircleOnDestination(
                    new Ray3(input.car.position, input.car.orientation.noseVector),
                    input.car.orientation.roofVector,
                    input.car.velocity.magnitude());

            isDrifting = rightTurn.center.offset.minus(destination).magnitude()
                    < rightTurn.radii/4;
            //output.drift(isDrifting);
        }
        else {
            Circle3D leftTurn = GroundTrajectoryFinder.getLeftTurnCircleOnDestination(
                    new Ray3(input.car.position, input.car.orientation.noseVector),
                    input.car.orientation.roofVector,
                    input.car.velocity.magnitude());

            isDrifting = leftTurn.center.offset.minus(destination).magnitude()
                    < leftTurn.radii/4;
            //output.drift(isDrifting);
        }
        output.drift((Math.abs(turningRate) > 7
                || isDrifting)
                && input.car.spin.toFrameOfReference(input.car.orientation).z * turningRate > 2);

        //System.out.println(turningRate);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(destination, Color.blue);
    }
}
