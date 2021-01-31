package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.dribble_recovery.recovery_nested_states.catch_rolling_ball_states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.Dribble5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.MaxAccelerationFromThrottleFinder;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.state_machine.State;

import java.awt.*;

public class GiveItANudgeState implements State {

    private final BotBehaviour bot;
    private final Dribble5 masterDribbleController;

    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController groundOrientationController;

    private MovingPoint destination;

    public GiveItANudgeState(BotBehaviour bot, Dribble5 masterDribbleController) {
        this.bot = bot;
        this.masterDribbleController = masterDribbleController;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);

        this.destination = new MovingPoint();
    }

    @Override
    public void exec(DataPacket input) {
        destination = RawBallTrajectory.trajectory
                .remove(movingPoint -> movingPoint.currentState.direction.z <= -40)
                .remove(movingPoint -> {
                    double distanceFromBall = movingPoint.currentState.offset
                            .distance(input.car.hitBox
                                    .closestPointOnSurface(movingPoint.currentState.offset));
                    double timeToReach = movingPoint.time;
                    double speedToReach = distanceFromBall/timeToReach;

                    return speedToReach > RlConstants.CAR_MAX_SPEED;
                })
                .remove(movingPoint -> {
                    double distanceFromBall = movingPoint.currentState.offset
                            .distance(input.car.hitBox
                                    .closestPointOnSurface(movingPoint.currentState.offset));
                    double timeToReach = movingPoint.time;
                    double speedToReach = distanceFromBall/timeToReach;
                    double accelerationToReach = (speedToReach - input.car.velocity.magnitude())/timeToReach;
                    double averageSpeedToReach = (speedToReach + input.car.velocity.magnitude())/2;
                    double orientationScalarToTheBall = input.car.orientation.noseVector
                            .dotProduct(movingPoint.currentState.offset
                                    .minus(input.car.position).normalized());
                    if(accelerationToReach >
                            RlConstants.ACCELERATION_DUE_TO_BOOST*(input.car.boost/100)
                            + MaxAccelerationFromThrottleFinder.compute(averageSpeedToReach)) {
                        return true;
                    }
                    if(speedToReach > distanceFromBall*2.4) {
                        return true;
                    }
                    return false;
                })
                .firstValid(5, 1.0/RawBallTrajectory.PREDICTION_REFRESH_RATE);
        if(destination == null) {
            return;
        }

        double desiredSpeed = destination.currentState.offset.distance(input.car.position)/destination.time;
        double accelerationToReach = (desiredSpeed - input.car.velocity.magnitude())/destination.time;
        boolean isBoosting = accelerationToReach > MaxAccelerationFromThrottleFinder.compute(input.car.velocity.magnitude())
                && input.car.velocity.magnitude() < 2290;
        bot.output().boost(isBoosting);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(destination.currentState.offset);
        groundOrientationController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        double speedOfBallTowardCar = input.ball.velocity.minus(input.car.velocity)
                .dotProduct(input.car.position
                        .minus(input.ball.position)
                        .normalized());
        double speedOfBallInZ = input.ball.velocity.z;
        if(speedOfBallTowardCar < -5
            && Math.abs(speedOfBallInZ) < 10) {
            bot.output().boost(false);
            return new SwipeState(bot, masterDribbleController);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("give it a nudge", Color.YELLOW, input.car.position, 1, 1);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        if(destination != null) {
            shapeRenderer.renderCross(destination.currentState.offset, Color.CYAN);
        }
    }
}
