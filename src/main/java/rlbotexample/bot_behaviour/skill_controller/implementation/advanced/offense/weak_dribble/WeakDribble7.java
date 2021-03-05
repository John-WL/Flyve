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
import util.renderers.ShapeRenderer;

import java.awt.*;
import java.util.function.Function;

// I put back the algorithm into the updateOutput function instead of the debug one.
public class WeakDribble7 extends SkillController {

    private BotBehaviour botBehaviour;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController2 groundOrientationController;

    private Function<Double, Vector3> ballDestination;

    private Trajectory3D ballTrajectory;
    private Trajectory3D trajectoryOfBallDestinationFromBall;
    private Trajectory3D playerTrajectory;

    private double desiredSpeed;
    private Vector3 playerDestination;

    public WeakDribble7(BotBehaviour bot) {
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
        ballTrajectory = input.statePrediction.ballAsTrajectory()
                .modify(m -> m.physicsState.offset.plus(m.physicsState.offset.minus(ballDestination.apply(m.time)).scaledToMagnitude(RlConstants.BALL_RADIUS)));
        //ballTrajectory = t -> new Vector3(0, 0, 100);
        trajectoryOfBallDestinationFromBall = t -> ballDestination.apply(t).minus(ballTrajectory.apply(t));
        final Trajectory3D playerRightTurnDestination = t -> {
            final Vector3 ballPosition = ballTrajectory.apply(t);
            final Vector3 ballDesiredDirection = trajectoryOfBallDestinationFromBall.apply(t)
                    .normalized();
            final Trajectory3D playerDestinationOnRightTurn = GroundTrajectoryFinder.getLeftTurningTrajectory(
                    new Ray3(ballPosition, ballDesiredDirection.scaled(-1)),
                    input.car.orientation.roofVector,
                    desiredSpeed);
            return playerDestinationOnRightTurn.apply(t);
        };
        final Trajectory3D playerLeftTurnDestination = t -> {
            final Vector3 ballPosition = ballTrajectory.apply(t);
            final Vector3 ballDesiredDirection = trajectoryOfBallDestinationFromBall.apply(t)
                    .normalized();
            final Trajectory3D playerDestinationOnLeftTurn = GroundTrajectoryFinder.getRightTurningTrajectory(
                    new Ray3(ballPosition, ballDesiredDirection.scaled(-1)),
                    input.car.orientation.roofVector,
                    desiredSpeed);
            return playerDestinationOnLeftTurn.apply(t);
        };

        double bestQRight = findBestQ(playerRightTurnDestination, input);
        double bestQLeft = findBestQ(playerLeftTurnDestination, input);

        // testing out the formula
        double Q = 4;
        Trajectory3D S = playerRightTurnDestination;
        double plannedSpeed = VT(XT(XQ(Q), XSPrime(Q, S, input)), T(Q, XSPrime(Q, S, input)));
        //System.out.println(plannedSpeed);

        //if(bestQRight < bestQLeft) {
        if(true) {
            playerTrajectory = playerRightTurnDestination;
            playerDestination = playerRightTurnDestination.apply(bestQRight);
        }
        else {
            playerTrajectory = playerLeftTurnDestination;
            playerDestination = playerLeftTurnDestination.apply(bestQLeft);
        }

        groundOrientationController.setDestination(playerDestination);
        groundOrientationController.updateOutput(input);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);
    }

    private double findBestQ(Trajectory3D S, DataPacket input) {
        final double amountOfTimeToTest = 5;
        double bestVT = Double.MAX_VALUE;
        double bestQ = Double.MAX_VALUE;

        for(int i = 0; i < amountOfTimeToTest*RlConstants.BOT_REFRESH_RATE; i++) {
            double Q = i/RlConstants.BOT_REFRESH_RATE;
            double XQ = XQ(Q);

            double XSPrime = XSPrime(Q, S, input);

            double XT = XT(XQ, XSPrime);
            double T = T(Q, XSPrime);
            double VT = VT(XT, T);

            if(Math.abs(desiredSpeed - VT) < Math.abs(desiredSpeed - bestVT)) {
                bestVT = VT;
                bestQ = Q;
            }
        }

        return bestQ;
    }

    private double XQ(double Q) {
        return Q * desiredSpeed; // Only true when the car is going at the right speed in the right direction.
        // Maybe there's a way to get the real value of XQ with a dot product of the car's speed?
        // Is the system more stable if we do that?
        // Maybe we need to "take into account" the acceleration by assuming it's going to be constant? (and so we would
        // compute the average between the dot product of the speed of the car with the desired direction and the desired speed)
    }

    private double XSPrime(double Q, Trajectory3D S, DataPacket input) {
        return S.apply(Q).minus(input.car.position).magnitude();
    }

    private double XT(double XQ, double XSPrime) {
        return XQ + XSPrime;
    }

    private double T(double Q, double XSPrime) {
        return Q + (XSPrime / desiredSpeed);
    }

    private double VT(double XT, double T) {
        return XT/T;
    }

    @Override
    public void setupController() {
        // remnants of the previous iteration lol
        //exponentCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_EXPONENT_COEF);
        //aCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_A_COEF);
        //desiredSpeed = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.WEAK_DRIBBLE_DESIRED_SPEED);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        // renderer
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(playerTrajectory, 5, Color.CYAN);
        shapeRenderer.renderCross(playerDestination, Color.GREEN);
    }
}
