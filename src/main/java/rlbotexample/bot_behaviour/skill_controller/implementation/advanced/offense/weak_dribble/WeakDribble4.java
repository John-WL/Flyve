package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.weak_dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectoryFinder;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.parameter_configuration.ArbitraryValueSerializer;
import util.renderers.ShapeRenderer;
import util.shapes.Circle3D;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Deprecated
public class WeakDribble4 extends SkillController {

    private BotBehaviour botBehaviour;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController2 groundOrientationController;

    private Function<Double, Vector3> ballDestination;

    private double exponentCoef = 0;
    private double aCoef = 0;

    private double desiredSpeed;

    public WeakDribble4(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController2(bot);

        this.ballDestination = t -> new Vector3();

        this.desiredSpeed = 1800;
    }

    public void setBallDestination(Function<Double, Vector3> ballDestination) {
        this.ballDestination = ballDestination;
    }

    public void setSpeed(double desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    @Override
    public void updateOutput(DataPacket input) {
    }

    @Override
    public void setupController() {
        exponentCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_EXPONENT_COEF);
        aCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_A_COEF);
        desiredSpeed = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_DESIRED_SPEED);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        final Circle3D rightTurnCircle = GroundTrajectoryFinder.getRightTurnCircleOnDestination(
                new Ray3(input.car.position, input.car.orientation.noseVector),
                input.car.orientation.roofVector,
                desiredSpeed);
        final Circle3D leftTurnCircle = GroundTrajectoryFinder.getLeftTurnCircleOnDestination(
                new Ray3(input.car.position, input.car.orientation.noseVector),
                input.car.orientation.roofVector,
                desiredSpeed);

        final Trajectory3D ballTrajectory = input.statePrediction.ballAsTrajectory()
                .modify(m -> m.physicsState.offset.plus(m.physicsState.offset.minus(ballDestination.apply(m.time)).scaledToMagnitude(RlConstants.BALL_RADIUS)));
        //final Trajectory3D ballTrajectory = t -> new Vector3(0, 0, 100);
        final Trajectory3D rightTurn = GroundTrajectoryFinder.getRightTurningTrajectory(input.car);
        final Trajectory3D leftTurn = GroundTrajectoryFinder.getLeftTurningTrajectory(input.car);
        final Trajectory3D trajectoryOfBallDestinationFromBall = t -> ballDestination.apply(t).minus(ballTrajectory.apply(t));;
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
            Vector3 rightPoint = trajectoryOfPointOnRightCircleToAlign.apply(t);

            Vector3 leftPoint = trajectoryOfPointOnLeftCircleToAlign.apply(t);

            if(rightPoint.minus(ballTrajectory.apply(t)).magnitudeSquared()
                    < leftPoint.minus(ballTrajectory.apply(t)).magnitudeSquared()) {
                //atomicOfCurrentlyUsedTurningCircle.set(rightTurnCircle);
                //return rightPoint;
            }
            atomicOfCurrentlyUsedTurningCircle.set(leftTurnCircle);
            return leftPoint;
        };

        // find the base time for the algorithm
        double tTilde = findTTilde(
                atomicOfCurrentlyUsedTurningCircle,
                trajectoryOfPointOnTurningCircle,
                input.car.position,
                nullVar -> atomicOfCurrentlyUsedTurningCircle.get().equals(rightTurnCircle));

        // player position when circle and ball meet for the first time
        Vector3 Pc = trajectoryOfPointOnTurningCircle.apply(tTilde);

        // find the exact time of reaching the destination
        double T = findT(ballTrajectory, Pc, tTilde);

        // actual ball position when player reaches the ball
        Vector3 Pb = ballTrajectory.apply(T);

        // distance between player final ball position and future player position on circle at tTilde
        Vector3 Pq = Pb.minus(Pc);

        double offsetScalar = Math.pow(exponentCoef, aCoef * Pb.minus(Pb.projectOnto(input.car.orientation.roofVector)).distance(Pc.minus(Pc.projectOnto(input.car.orientation.roofVector))));
        Vector3 centerOfTurningCircle = atomicOfCurrentlyUsedTurningCircle.get().center.offset;
        Vector3 destinationOffset = centerOfTurningCircle.minus(input.car.position).normalized()
                .scaled(offsetScalar)
                .scaled(70);

        groundOrientationController.setDestination(Pq.plus(destinationOffset).plus(input.car.position));
        groundOrientationController.updateOutput(input);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);

        botBehaviour.output().boost(false);
        if(input.car.velocity.magnitude() < desiredSpeed) {
            //botBehaviour.output().boost(true);
        }

        //////////////////////////////////////////////////////////////////////
        // renderer
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCircle3D(rightTurnCircle, Color.CYAN);
        shapeRenderer.renderCircle3D(leftTurnCircle, Color.CYAN);
        shapeRenderer.renderTrajectory(input.statePrediction.ballAsTrajectory(), 3, Color.MAGENTA);
        shapeRenderer.renderTrajectory(trajectoryOfPointOnTurningCircle, 3, Color.GREEN);
        //renderer.drawLine3d(Color.GREEN, trajectoryOfBallDestinationFromBall.apply(1.0).plus(ballTrajectory.apply(1.0)).toFlatVector(), ballTrajectory.apply(1.0).toFlatVector());
        shapeRenderer.renderCross(Pq.plus(destinationOffset).plus(input.car.position), Color.red);
        shapeRenderer.renderCross(Pq.plus(input.car.position), Color.orange);
        Vector3 PbPrime = ballTrajectory.apply(tTilde);
        shapeRenderer.renderCross(Pb, Color.MAGENTA);
        shapeRenderer.renderCross(Pc, Color.GREEN);
    }

    private double findTTilde(AtomicReference<Circle3D> turningCircle,
                            Trajectory3D trajectoryOfPointOnTurningCircle,
                            Vector3 carPosition,
                            Function<Void, Boolean> isRightTurn) {
        final double amountOfTimeToTest = 5;
        double bestTTest = Double.MAX_VALUE;

        for(int i = 0; i < amountOfTimeToTest*RlConstants.BOT_REFRESH_RATE; i++) {
            double tTest = i/RlConstants.BOT_REFRESH_RATE;
            // this updates the currently used turning circle. Please don't question my questionable design
            Vector3 currentPointOnTurningCircle = trajectoryOfPointOnTurningCircle.apply(tTest);
            Circle3D currentTurningCircle = turningCircle.get();
            double tTilde = findTTilde(
                    currentTurningCircle,
                    currentPointOnTurningCircle,
                    carPosition,
                    desiredSpeed,
                    isRightTurn);
            if(Math.abs(tTest - tTilde) < Math.abs(bestTTest - tTilde)) {
                bestTTest = tTest;
            }
        }

        return bestTTest;
    }

    private double findT(Trajectory3D ballTrajectory, Vector3 Pc, double tTilde) {
        final double amountOfTimeToTest = 5;
        double bestCorrespondingSpeed = Double.MAX_VALUE;
        double bestT = Double.MAX_VALUE;

        int initialValueOfI = (int)(tTilde*RlConstants.BOT_REFRESH_RATE);
        for(int i = initialValueOfI; i < amountOfTimeToTest*RlConstants.BOT_REFRESH_RATE; i++) {
            double tTest = i/RlConstants.BOT_REFRESH_RATE;
            Vector3 PbTest = ballTrajectory.apply(tTest);
            Vector3 PqTest = PbTest.minus(Pc);
            double correspondingSpeed = PqTest.magnitude() / (tTest - tTilde);
            if(Math.abs(correspondingSpeed - desiredSpeed) < Math.abs(bestCorrespondingSpeed - desiredSpeed)) {
                bestCorrespondingSpeed = correspondingSpeed;
                bestT = tTest;
            }
        }

        return bestT;
    }

    private double findTTilde(Circle3D turningCircle,
                              Vector3 pointOnTurningCircle,
                              Vector3 carPosition,
                              double carSpeed,
                              Function<Void, Boolean> isRightTurn) {
        double angleOfCar = turningCircle.findRadsFromClosestPoint(carPosition);
        double angleOfPointOnTurningCircle = turningCircle.findRadsFromClosestPoint(pointOnTurningCircle);
        double separationAngle;
        if(isRightTurn.apply(null)) {
            separationAngle = findSeparationAngleBetween(angleOfCar, angleOfPointOnTurningCircle);
        }
        else {
            separationAngle = findSeparationAngleBetween(angleOfPointOnTurningCircle, angleOfCar);
        }
        double arcLength = separationAngle * turningCircle.radii;

        return arcLength/carSpeed;
    }

    private double findSeparationAngleBetween(double angle1, double angle2) {
        if(angle1 < angle2) {
            angle1 = angle1 + Math.PI*2;
        }

        return angle1 - angle2;
    }
}
