package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectoryFinder;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GroundDirectionalHit2 extends SkillController {

    private BotBehaviour botBehaviour;
    private Vector3 ballDestination;
    private Vector3 playerDestination;
    private Trajectory3D rightTurnTrajectory;
    private Trajectory3D leftTurnTrajectory;
    private HitBox futureHitBox;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    public GroundDirectionalHit2(BotBehaviour bot) {
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
        rightTurnTrajectory = GroundTrajectoryFinder.getRightTurningTrajectory(input.car);
        leftTurnTrajectory = GroundTrajectoryFinder.getLeftTurningTrajectory(input.car);
        Trajectory3D ballTrajectory = time -> {
            Vector3 ballPosition = input.statePrediction.ballAtTime(time).position;
            Vector3 offset = ballDestination.minus(ballPosition).scaledToMagnitude(RlConstants.BALL_RADIUS*0.5);
            return ballPosition.minus(offset);
        };
        double closestApproachTimeForRightTurn = Trajectory3D.findTimeOfClosestApproach(rightTurnTrajectory, ballTrajectory, 5, 0.33333);
        //double closestApproachTimeForRightTurn = 1;
        Vector3 playerDestinationRight = ballTrajectory.apply(closestApproachTimeForRightTurn).minus(rightTurnTrajectory.apply(closestApproachTimeForRightTurn))
                .plus(input.car.position);
        double closestApproachTimeForLeftTurn = Trajectory3D.findTimeOfClosestApproach(leftTurnTrajectory, ballTrajectory, 5, 0.33333);
        //double closestApproachTimeForLeftTurn = 1;
        Vector3 playerDestinationLeft = ballTrajectory.apply(closestApproachTimeForLeftTurn).minus(leftTurnTrajectory.apply(closestApproachTimeForLeftTurn))
                .plus(input.car.position);

        if(playerDestinationRight.magnitude() < playerDestinationLeft.magnitude()) {
            playerDestination = playerDestinationRight;
        }
        else {
            playerDestination = playerDestinationLeft;
        }


        double driveSpeed = 1500;
        drivingSpeedController.setSpeed(driveSpeed);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(playerDestination);
        groundOrientationController.updateOutput(input);
        if(playerDestination == playerDestinationRight
                && playerDestination.minus(input.car.position).magnitude() < 700) {
            botBehaviour.output().steer(1);
            botBehaviour.output().drift(false);
        }
        if(playerDestination == playerDestinationLeft
                && playerDestination.minus(input.car.position).magnitude() < 700) {
            botBehaviour.output().steer(-1);
            botBehaviour.output().drift(false);
        }

        if(playerDestination.minus(input.car.position).magnitude() > 2000) {
            botBehaviour.output().drift(false);
        }
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.CYAN, playerDestination.toFlatVector(), input.car.position.toFlatVector());
        renderer.drawLine3d(Color.red, input.ball.position.toFlatVector(), ballDestination.toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        shapeRenderer.renderTrajectory(rightTurnTrajectory, 3, Color.CYAN);
        shapeRenderer.renderTrajectory(leftTurnTrajectory, 3, Color.CYAN);
    }
}
