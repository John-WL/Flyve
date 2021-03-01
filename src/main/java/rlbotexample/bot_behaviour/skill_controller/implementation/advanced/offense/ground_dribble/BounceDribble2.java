package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.GroundTrajectoryFinder;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle3D;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class BounceDribble2 extends SkillController {

    private BotBehaviour botBehaviour;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    private Vector3 ballDestination;
    private Vector3 alignmentOffset;

    private Trajectory3D turnTangent;

    public BounceDribble2(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.ballDestination = new Vector3();
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        final Circle3D rightTurnCircle = GroundTrajectoryFinder.getRightTurnCircleOnDestination(
                new Ray3(input.car.position, input.car.orientation.noseVector),
                input.car.orientation.roofVector,
                input.car.velocity.magnitude());
        final Circle3D leftTurnCircle = GroundTrajectoryFinder.getLeftTurnCircleOnDestination(
                new Ray3(input.car.position, input.car.orientation.noseVector),
                input.car.orientation.roofVector,
                input.car.velocity.magnitude());

        final Trajectory3D ballTrajectory = input.statePrediction.ballAsTrajectory();
        final Trajectory3D rightTurn = GroundTrajectoryFinder.getRightTurningTrajectory(input.car);
        final Trajectory3D leftTurn = GroundTrajectoryFinder.getLeftTurningTrajectory(input.car);
        final Trajectory3D trajectoryOfBallDestinationFromBall = t -> ballDestination.minus(ballTrajectory.apply(t));
        final Trajectory3D trajectoryOfRightRotationToFindRightTangent = t -> trajectoryOfBallDestinationFromBall.apply(t)
                .rotate(input.car.orientation.roofVector.scaled(0.5*Math.PI))
                .scaledToMagnitude(1000) // making sure the length is fine
                .plus(rightTurnCircle.center.offset);
        final Trajectory3D trajectoryOfLeftRotationToFindLeftTangent = t -> trajectoryOfBallDestinationFromBall.apply(t)
                .rotate(input.car.orientation.roofVector.scaled(-0.5*Math.PI))
                .scaledToMagnitude(1000) // making sure the length is fine #2
                .plus(leftTurnCircle.center.offset);
        final Trajectory3D trajectoryOfPointOnRightCircleToAlign = t -> rightTurnCircle.findClosestPointFrom(
                trajectoryOfRightRotationToFindRightTangent.apply(t));
        final Trajectory3D trajectoryOfPointOnLeftCircleToAlign = t -> leftTurnCircle.findClosestPointFrom(
                trajectoryOfLeftRotationToFindLeftTangent.apply(t));
        // ew
        final AtomicReference<Circle3D> atomicOfCurrentlyUsedTurningCircle = new AtomicReference<>();
        final Trajectory3D trajectoryOfPointOnTurningCircle = t -> {
            Vector3 rightPoint = trajectoryOfPointOnRightCircleToAlign.apply(t)
                    .minus(ballTrajectory.apply(t));
            Vector3 leftPoint = trajectoryOfPointOnLeftCircleToAlign.apply(t)
                    .minus(ballTrajectory.apply(t));
            if(rightPoint.magnitudeSquared() > leftPoint.magnitudeSquared()) {
                atomicOfCurrentlyUsedTurningCircle.set(rightTurnCircle);
                return rightPoint;
            }
            atomicOfCurrentlyUsedTurningCircle.set(leftTurnCircle);
            return leftPoint;
        };
        Trajectory3D trajectoryOfBallMinusPointToAlign = t -> ballTrajectory.apply(t)
                .minus(trajectoryOfPointOnTurningCircle.apply(t));
        Trajectory3D trajectoryOfUnitVectorZWithLengthOfCircumferenceOfTurningCircle = t ->
                Vector3.UP_VECTOR
                        .scaled((
                                Math.abs(
                                        atomicOfCurrentlyUsedTurningCircle.get().findRadsFromClosestPoint(
                                                input.car.position)
                                                - atomicOfCurrentlyUsedTurningCircle.get().findRadsFromClosestPoint(
                                                        trajectoryOfPointOnTurningCircle.apply(t))))
                                * atomicOfCurrentlyUsedTurningCircle.get().radii);
        Function<Double, Double> lengthOfTrack = t -> trajectoryOfBallMinusPointToAlign.apply(t).magnitude()
                + trajectoryOfUnitVectorZWithLengthOfCircumferenceOfTurningCircle.apply(t).magnitude();

        double desiredSpeed = 1200;

        Function<Double, Double> amountOfTimeToReachDestinationAtTime = timeToConsider -> lengthOfTrack.apply(timeToConsider)/desiredSpeed;

        double bestTime = bestFit(amountOfTimeToReachDestinationAtTime, 5, 1/RlConstants.BOT_REFRESH_RATE);
        Vector3 bestPointToAlign = trajectoryOfPointOnTurningCircle.apply(bestTime);
        Vector3 bestFutureBall = ballTrajectory.apply(bestTime);

        alignmentOffset = bestFutureBall.minus(bestPointToAlign)
                .plus(input.car.position);

        groundOrientationController.setDestination(alignmentOffset);
        groundOrientationController.updateOutput(input);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);


        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCircle3D(rightTurnCircle, Color.CYAN);
        shapeRenderer.renderCircle3D(leftTurnCircle, Color.CYAN);
        //shapeRenderer.renderTrajectory(input.statePrediction.ballAsTrajectory(), 3, Color.MAGENTA);
        shapeRenderer.renderTrajectory(turnTangent, 3, Color.red);

        renderer.drawLine3d(Color.GREEN, bestPointToAlign.toFlatVector(), input.car.position.toFlatVector());
        shapeRenderer.renderCross(bestFutureBall, Color.orange);
        shapeRenderer.renderCross(bestPointToAlign, Color.BLACK);
    }

    private double bestFit(Function<Double, Double> amountOfTimeToReachDestinationAtTime, double amountOfTime, double precision) {
        double bestConsideredTimeSoFar = Double.MAX_VALUE;
        double bestTimeToReachDestinationSoFar = Double.MAX_VALUE;

        for(int i = 0; i*precision < amountOfTime; i++) {
            double t = i*precision;
            double timeToReachDestination = amountOfTimeToReachDestinationAtTime.apply(t);
            if(bestTimeToReachDestinationSoFar > timeToReachDestination) {
                bestConsideredTimeSoFar = t;
                bestTimeToReachDestinationSoFar = timeToReachDestination;
            }
        }

        return bestConsideredTimeSoFar;
    }
}
