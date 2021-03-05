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
import java.util.function.Function;

// I put back the algorithm into the updateOutput function instead of the debug one.
public class WeakDribble6 extends SkillController {

    private BotBehaviour botBehaviour;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController2 groundOrientationController;

    private Function<Double, Vector3> ballDestination;
    private Vector3 destinationOffset;

    private double exponentCoef = 1.7;
    private double aCoef = -0.03;

    private double tTilde;
    private double T;
    private Vector3 Pc;
    private Vector3 Pb;
    private Vector3 Pq;
    private Circle3D usedCircle;

    private Circle3D rightTurnCircle;
    private Circle3D leftTurnCircle;

    private Trajectory3D trajectoryOfPointOnRightCircleToAlign;
    private Trajectory3D trajectoryOfPointOnLeftCircleToAlign;
    private Trajectory3D ballTrajectory;
    private Trajectory3D trajectoryOfBallDestinationFromBall;

    private double desiredSpeed;

    public WeakDribble6(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController2(bot);

        this.ballDestination = t -> new Vector3();

        this.desiredSpeed = 1200;
    }

    public void setBallImpulse(Function<Double, Vector3> ballDestination) {
        this.ballDestination = ballDestination;
    }

    public void setSpeed(double desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    @Override
    public void updateOutput(DataPacket input) {
        rightTurnCircle = GroundTrajectoryFinder.getRightTurnCircleOnDestination(
                new Ray3(input.car.position, input.car.orientation.noseVector),
                input.car.orientation.roofVector,
                desiredSpeed);
        leftTurnCircle = GroundTrajectoryFinder.getLeftTurnCircleOnDestination(
                new Ray3(input.car.position, input.car.orientation.noseVector),
                input.car.orientation.roofVector,
                desiredSpeed);

        ballTrajectory = input.statePrediction.ballAsTrajectory()
                .modify(m -> m.physicsState.offset.plus(m.physicsState.offset.minus(ballDestination.apply(m.time)).scaledToMagnitude(RlConstants.BALL_RADIUS)));
        //ballTrajectory = t -> new Vector3(0, 0, 100);
        //final Trajectory3D rightTurn = GroundTrajectoryFinder.getRightTurningTrajectory(input.car);
        //final Trajectory3D leftTurn = GroundTrajectoryFinder.getLeftTurningTrajectory(input.car);
        trajectoryOfBallDestinationFromBall = t -> ballDestination.apply(t).minus(ballTrajectory.apply(t));;
        final Trajectory3D trajectoryOfRightRotationToFindRightTangent = t -> trajectoryOfBallDestinationFromBall.apply(t)
                .rotate(input.car.orientation.roofVector.scaled(0.5*Math.PI))
                .scaledToMagnitude(1000) // making sure the length is fine
                .plus(rightTurnCircle.center.offset);
        final Trajectory3D trajectoryOfLeftRotationToFindLeftTangent = t -> trajectoryOfBallDestinationFromBall.apply(t)
                .rotate(input.car.orientation.roofVector.scaled(-0.5*Math.PI))
                .scaledToMagnitude(1000) // making sure the length is fine #2
                .plus(leftTurnCircle.center.offset);
        trajectoryOfPointOnRightCircleToAlign = t -> rightTurnCircle.findClosestPointFrom(
                trajectoryOfRightRotationToFindRightTangent.apply(t));
        trajectoryOfPointOnLeftCircleToAlign = t -> leftTurnCircle.findClosestPointFrom(
                trajectoryOfLeftRotationToFindLeftTangent.apply(t));

        // this part is the main difference from WeakDribble4
        // solve for right circle
        double tTildeRight = findTTilde(
                rightTurnCircle,
                trajectoryOfPointOnRightCircleToAlign,
                input.car.position,
                true);
        Vector3 PcRight = trajectoryOfPointOnRightCircleToAlign.apply(tTildeRight);
        double TRight = findT(ballTrajectory, PcRight, tTildeRight);
        Vector3 PbRight = ballTrajectory.apply(TRight);
        Vector3 PqRight = PbRight.minus(PcRight);

        // solve for left circle
        double tTildeLeft = findTTilde(
                leftTurnCircle,
                trajectoryOfPointOnLeftCircleToAlign,
                input.car.position,
                false);
        Vector3 PcLeft = trajectoryOfPointOnLeftCircleToAlign.apply(tTildeLeft);
        double TLeft = findT(ballTrajectory, PcLeft, tTildeLeft);
        Vector3 PbLeft = ballTrajectory.apply(TLeft);
        Vector3 PqLeft = PbLeft.minus(PcLeft);

        // chose the best circle
        if(TRight - tTildeRight < TLeft - tTildeLeft) {
            tTilde = tTildeRight;
            T = TRight;
            Pc = PcRight;
            Pb = PbRight;
            Pq = PqRight;
            usedCircle = rightTurnCircle;
        }
        else {
            tTilde = tTildeLeft;
            T = TLeft;
            Pc = PcLeft;
            Pb = PbLeft;
            Pq = PqLeft;
            usedCircle = leftTurnCircle;
        }

        double offsetScalar = Math.pow(exponentCoef, aCoef * Pb.minus(Pb.projectOnto(input.car.orientation.roofVector)).distance(Pc.minus(Pc.projectOnto(input.car.orientation.roofVector))));
        Vector3 centerOfTurningCircle = usedCircle.center.offset;
        destinationOffset = centerOfTurningCircle.minus(input.car.position).normalized()
                .scaled(offsetScalar)
                .scaled(200);

        groundOrientationController.setDestination(Pq.plus(destinationOffset).plus(input.car.position));
        groundOrientationController.updateOutput(input);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);
    }

    @Override
    public void setupController() {
        exponentCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_EXPONENT_COEF);
        aCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_A_COEF);
        //desiredSpeed = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_DESIRED_SPEED);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        // renderer
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCircle3D(rightTurnCircle, Color.CYAN);
        shapeRenderer.renderCircle3D(leftTurnCircle, Color.CYAN);
        shapeRenderer.renderTrajectory(input.statePrediction.ballAsTrajectory(), 3, Color.MAGENTA);
        shapeRenderer.renderTrajectory(ballDestination, 3, Color.GREEN);
        //shapeRenderer.renderTrajectory(trajectoryOfPointOnLeftCircleToAlign, 3, Color.GREEN);
        //renderer.drawLine3d(Color.GREEN, trajectoryOfBallDestinationFromBall.apply(1.0).plus(ballTrajectory.apply(1.0)).toFlatVector(), ballTrajectory.apply(1.0).toFlatVector());
        shapeRenderer.renderCross(Pq.plus(destinationOffset).plus(input.car.position), Color.red);
        shapeRenderer.renderCross(Pq.plus(input.car.position), Color.orange);
        Vector3 PbPrime = ballTrajectory.apply(tTilde);
        shapeRenderer.renderCross(Pb, Color.MAGENTA);
        shapeRenderer.renderCross(Pc, Color.GREEN);
    }

    private double findTTilde(Circle3D turningCircle,
                            Trajectory3D trajectoryOfPointOnTurningCircle,
                            Vector3 carPosition,
                            boolean isRightTurningCircle) {
        final double amountOfTimeToTest = 5;
        double bestTTest = Double.MAX_VALUE;

        for(int i = 0; i < amountOfTimeToTest*RlConstants.BOT_REFRESH_RATE; i++) {
            double tTest = i/RlConstants.BOT_REFRESH_RATE;
            // this updates the currently used turning circle. Please don't question my questionable design
            Vector3 currentPointOnTurningCircle = trajectoryOfPointOnTurningCircle.apply(tTest);
            double tTilde = findTTilde(
                    turningCircle,
                    currentPointOnTurningCircle,
                    carPosition,
                    desiredSpeed,
                    isRightTurningCircle);
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
                              boolean isRightTurningCircle) {
        double angleOfCar = turningCircle.findRadsFromClosestPoint(carPosition);
        double angleOfPointOnTurningCircle = turningCircle.findRadsFromClosestPoint(pointOnTurningCircle);
        double separationAngle;
        if(isRightTurningCircle) {
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
