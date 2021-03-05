package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.dynamic_data.ground.trajectories.DrivingTrajectoryInfo;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectoryFinder;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle3D;

import java.awt.*;

public class GroundDirectionalHit3 extends SkillController {

    private BotBehaviour botBehaviour;
    private Vector3 ballDestination;
    private Vector3 playerDestination;
    private HitBox futureHitBox;
    private Circle3D turnRadiusOnBall;
    private Circle3D turnRadiusOnCar;
    private DrivingTrajectoryInfo info;

    private Ray3 straightLineToTravel;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    public GroundDirectionalHit3(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {

        GroundTrajectoryFinder drivingTrajectoryFinder = new GroundTrajectoryFinder(
        // for the bot final destination at any point in time
                time -> input.statePrediction.ballAtTime(time).position,
        // the bot orientation at any destination point in time
                time -> ballDestination);

        info = drivingTrajectoryFinder.findDrivingTrajectoryInfo(input);

        turnRadiusOnBall = GroundTrajectoryFinder.getLeftTurnCircleOnDestination(new Ray3(input.statePrediction.ballAtTime(info.timeOfDriving).position, ballDestination.minus(input.statePrediction.ballAtTime(info.timeOfDriving).position).normalized()), input.car.orientation.roofVector, input.car.velocity.magnitude());

        turnRadiusOnCar = GroundTrajectoryFinder.getLeftTurnCircleOnDestination(new Ray3(input.car.position, input.car.orientation.noseVector), input.car.orientation.roofVector, input.car.velocity.magnitude());

        straightLineToTravel = new Ray3(info.turningPoint1, info.turningPoint2);

        //groundOrientationController.setDestination(info.turningPoint2);
        //groundOrientationController.updateOutput(input);
        double steerAmount = 0;
        if(input.car.orientation.noseVector
                .dotProduct(info.turningPoint2.minus(info.turningPoint1)
                        .minus(info.turningPoint2.minus(info.turningPoint1).projectOnto(input.car.orientation.roofVector))
                        .normalized()) < 0.97) {
            steerAmount = -1;
        }
        else if(info.turningPoint1.minus(info.turningPoint2).magnitude() < 20) {
            steerAmount = -1;
        }

        botBehaviour.output().steer(steerAmount);

        drivingSpeedController.setSpeed(1500);
        drivingSpeedController.updateOutput(input);
        //botBehaviour.output().boost(true);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        //renderer.drawLine3d(Color.CYAN, playerDestination, input.car.position);
        renderer.drawLine3d(Color.red, input.ball.position.toFlatVector(), ballDestination.toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        shapeRenderer.renderCircle3D(turnRadiusOnBall, Color.green);
        shapeRenderer.renderCircle3D(turnRadiusOnCar, Color.CYAN);
        renderer.drawString3d(info.timeOfDriving + "", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
        renderer.drawLine3d(Color.magenta, straightLineToTravel.offset.toFlatVector(), straightLineToTravel.direction.toFlatVector());
    }
}
