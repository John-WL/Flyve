package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.dribble_recovery.recovery_nested_states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.Dribble5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.DribbleUtils;
import rlbotexample.input.dynamic_data.ground.MaxAccelerationFromThrottleFinder;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;

public class CatchBouncyBallState implements State {

    private final BotBehaviour bot;
    private final Dribble5 masterDribbleController;

    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController groundOrientationController;

    private Vector3 actualDestination;
    private MovingPoint firstValidBall;

    public CatchBouncyBallState(BotBehaviour bot, Dribble5 masterDribbleController) {
        this.bot = bot;
        this.masterDribbleController = masterDribbleController;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);

        this.actualDestination = new Vector3();
        this.firstValidBall = new MovingPoint();
    }

    @Override
    public void exec(DataPacket input) {
        final double maxBallHeight = RlConstants.BALL_RADIUS + (RlConstants.OCTANE_ROOF_ELEVATION_WHEN_DRIVING);
        firstValidBall = RawBallTrajectory.trajectory
                .remove(movingPoint -> movingPoint.currentState.offset.z > maxBallHeight)
                .remove(movingPoint -> input.car.position
                        .distance(movingPoint.currentState.offset) / movingPoint.time
                        > RlConstants.CAR_MAX_SPEED/2)
                .firstValid(5, 1.0/RawBallTrajectory.PREDICTION_REFRESH_RATE);
        // if there's no valid ball, then we can't do anything, so we return
        // this only happens when the ball is going into net and is not gonna bounce
        // on the ground anymore, so I think it's fine
        if(firstValidBall == null) {
            return;
        }
        double timeOfHit = Trajectory3D.findTimeOfClosestApproachBetween(
                RawBallTrajectory.trajectory,
                t -> firstValidBall.currentState.offset,
                5,
                RawBallTrajectory.PREDICTION_REFRESH_RATE);
        actualDestination = firstValidBall.currentState.offset
                .plus(new Vector3(masterDribbleController.throttleAmount, masterDribbleController.steerAmount, 0)
                        .rotate(Vector3.UP_VECTOR
                                .scaled(input.car.orientation.noseVector
                                        .flatten().correctionAngle(new Vector2(1, 0))))
                        .scaled(30));

        double distanceFromBall = actualDestination
                .distance(input.car.position);
        double desiredSpeed = distanceFromBall/timeOfHit;
        //System.out.println(desiredSpeed);
        double accelerationToReach = (desiredSpeed - input.car.velocity.magnitude())/timeOfHit;
        boolean isBoosting = accelerationToReach > MaxAccelerationFromThrottleFinder.compute(input.car.velocity.magnitude())
                && input.car.velocity.magnitude() < 2290;

        drivingSpeedController.setSpeed(input.car.position.flatten().distance(actualDestination.flatten()) / timeOfHit);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(actualDestination);
        groundOrientationController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(!DribbleUtils.ballBouncingTooMuchForSwipe(input)) {
            bot.output().boost(false);
            return new CatchRollingBallState(bot, masterDribbleController);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("catch bouncy ball", Color.YELLOW, input.car.position, 1, 1);
        if(firstValidBall != null) {
            renderer.drawLine3d(Color.blue, actualDestination, firstValidBall.currentState.offset);
            renderer.drawRectangle3d(Color.blue, actualDestination, 10, 10, true);
        }
    }
}
