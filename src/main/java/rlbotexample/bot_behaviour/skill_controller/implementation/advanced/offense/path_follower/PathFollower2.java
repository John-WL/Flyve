package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.path_follower;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundSpinController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Ray3;
import util.math.vector.Vector;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle3D;

import java.awt.*;

// I put back the algorithm into the updateOutput function instead of the debug one.
public class PathFollower2 extends SkillController {

    private BotBehaviour botBehaviour;

    private DrivingSpeedController drivingSpeedController;
    private GroundSpinController groundSpinController;

    private Trajectory3D path;

    private double curvature;
    private double desiredSpeed;
    private double desiredSpin;
    private Vector3 positionOnPath;

    public PathFollower2(final BotBehaviour bot) {
        this.botBehaviour = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundSpinController = new GroundSpinController(bot);

        this.curvature = 0;
        this.desiredSpeed = 1200;
        this.desiredSpin = 0;
    }

    public void setPath(final Trajectory3D path) {
        this.path = path;
    }

    @Override
    public void updateOutput(final DataPacket input) {
        double timeOnPath = Trajectory3D.findTimeOfClosestApproach(path, t -> input.car.position, 0.5, RlConstants.BOT_REFRESH_RATE);

        //Vector3 positionOnPath = path.apply(timeOnPath);
        Vector3 positionOnPath = input.car.position;
        this.positionOnPath = positionOnPath;
        Vector3 nextPositionOnPath = path.apply(timeOnPath + RlConstants.BOT_REFRESH_RATE);
        Vector3 previousPositionOnPath = path.apply(timeOnPath - RlConstants.BOT_REFRESH_RATE);
        Vector3 derivative = positionOnPath.minus(nextPositionOnPath).scaled(RlConstants.BOT_REFRESH_RATE);

        desiredSpeed = derivative.magnitude();
        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);

        //curvature = findCurvature(positionOnPath, nextPositionOnPath, previousPositionOnPath);
        curvature = 1.0/1000;
        desiredSpin = curvature * input.car.velocity.magnitude() / (2 * Math.PI);
        groundSpinController.setSpin(desiredSpin);
        groundSpinController.updateOutput(input);

        System.out.println(desiredSpin);

    }

    private double findCurvature(Vector3 p1, Vector3 p2, Vector3 p3) {
        double a2 = p1.minus(p2).magnitudeSquared();
        double b2 = p1.minus(p3).magnitudeSquared();
        double c2 = p2.minus(p3).magnitudeSquared();

        double fourA = Math.sqrt(4*a2*b2 - sq(a2 + b2 - c2));

        return fourA / (Math.sqrt(a2) * Math.sqrt(b2) * Math.sqrt(c2));
    }

    private double sq(double x) {
        return x;
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(final Renderer renderer, final DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(path, 5, Color.green);
        shapeRenderer.renderCircle3D(new Circle3D(new Ray3(input.car.position, input.car.orientation.roofVector), 1/curvature), Color.CYAN);
        shapeRenderer.renderCross(path.apply(0.1), Color.MAGENTA);
    }
}
