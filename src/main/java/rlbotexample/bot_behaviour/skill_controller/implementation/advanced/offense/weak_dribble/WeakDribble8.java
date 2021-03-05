package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.weak_dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.trajectories.DrivingTrajectory;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;
import java.util.function.Function;

// I put back the algorithm into the updateOutput function instead of the debug one.
public class WeakDribble8 extends SkillController {

    private BotBehaviour botBehaviour;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController2 groundOrientationController;

    private Function<Double, Vector3> ballDestination;

    private Trajectory3D ballTrajectory;
    private Trajectory3D trajectoryOfBallDestinationFromBall;
    private Trajectory3D playerTrajectory;

    private double desiredSpeed;
    private Vector3 playerDestination;

    public WeakDribble8(final BotBehaviour bot) {
        this.botBehaviour = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController2(bot);

        this.ballDestination = t -> new Vector3();

        this.desiredSpeed = 1200;
    }

    public void setBallImpulse(final Function<Double, Vector3> ballDestination) {
        this.ballDestination = ballDestination;
    }

    public void setSpeed(final double desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    @Override
    public void updateOutput(final DataPacket input) {
        ballTrajectory = input.statePrediction.ballAsTrajectory()
                .modify(m -> m.physicsState.offset.plus(m.physicsState.offset.minus(ballDestination.apply(m.time)).scaledToMagnitude(RlConstants.BALL_RADIUS)));

        //ballTrajectory = t -> new Vector3(0, 0, 100);
        trajectoryOfBallDestinationFromBall = t -> ballDestination.apply(t).minus(ballTrajectory.apply(t));

        Function<Double, DrivingTrajectory> unsolvedRightTrajectory = t -> {
            final Vector3 destination = ballTrajectory.apply(t);
            final Vector3 orientation = trajectoryOfBallDestinationFromBall.apply(t).normalized();
            final Ray3 placementDestination = new Ray3(destination, orientation);
            return new DrivingTrajectory(placementDestination, input.car, desiredSpeed, null);
        };
        Function<Double, DrivingTrajectory> unsolvedLeftTrajectory = t -> {
            final Vector3 destination = ballTrajectory.apply(t);
            final Vector3 orientation = trajectoryOfBallDestinationFromBall.apply(t).normalized();
            final Ray3 placementDestination = new Ray3(destination, orientation);
            return new DrivingTrajectory(placementDestination, input.car, desiredSpeed, null);
        };
        Function<Double, DrivingTrajectory> unsolvedTrajectory = t -> {
            final DrivingTrajectory rightTrajectory = unsolvedRightTrajectory.apply(t);
            final DrivingTrajectory leftTrajectory = unsolvedLeftTrajectory.apply(t);

            if(rightTrajectory.totalTime < leftTrajectory.totalTime) {
                return rightTrajectory;
            }
            return leftTrajectory;
        };

        DrivingTrajectory solvedTrajectory = solveDrivingTrajectory(unsolvedTrajectory);
        Ray3 placementDestination = solvedTrajectory.apply(0.05);
        Vector3 destination = placementDestination.offset;//.plus(placementDestination.offset.scaled(-100));

        playerTrajectory = t -> solvedTrajectory.apply(t).offset;
        playerDestination = destination;

        groundOrientationController.setDestination(destination);
        groundOrientationController.updateOutput(input);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);
    }

    private DrivingTrajectory solveDrivingTrajectory(Function<Double, DrivingTrajectory> unsolvedTrajectory) {
        final double amountOfTimeToTest = 5;
        double bestTime = Double.MAX_VALUE;
        DrivingTrajectory bestTrajectory = unsolvedTrajectory.apply(0.0);

        for(int i = 1; i < amountOfTimeToTest*RlConstants.BOT_REFRESH_RATE; i++) {
            final double time = i/RlConstants.BOT_REFRESH_RATE;
            final DrivingTrajectory testTrajectory = unsolvedTrajectory.apply(time);

            if(Math.abs(time - testTrajectory.totalTime) < Math.abs(bestTime - testTrajectory.totalTime)) {
                bestTime = time;
                bestTrajectory = testTrajectory;
            }
        }

        return bestTrajectory;
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(final Renderer renderer, final DataPacket input) {
        // renderer
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(playerTrajectory, 5, Color.CYAN);
        shapeRenderer.renderCross(playerDestination, Color.GREEN);
    }
}
