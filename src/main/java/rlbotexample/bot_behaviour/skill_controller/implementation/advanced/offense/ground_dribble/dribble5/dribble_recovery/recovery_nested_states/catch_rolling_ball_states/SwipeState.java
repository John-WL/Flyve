package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.dribble_recovery.recovery_nested_states.catch_rolling_ball_states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.Dribble5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.MaxAccelerationFromThrottleFinder;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.state_machine.State;

import java.awt.*;

public class SwipeState implements State {

    private final BotBehaviour bot;
    private final Dribble5 masterDribbleController;

    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController groundOrientationController;

    private MovingPoint destination;

    public SwipeState(BotBehaviour bot, Dribble5 masterDribbleController) {
        this.bot = bot;
        this.masterDribbleController = masterDribbleController;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);

        this.destination = new MovingPoint();
    }

    @Override
    public void exec(DataPacket input) {
        destination = ((Trajectory3D) time -> {
            double leftOrRight = input.car.position.minus(RawBallTrajectory.ballAtTime(time).position)
                    .crossProduct(input.ball.velocity).z < 0 ? -1 : 1;
            return input.car.position.minus(RawBallTrajectory.ballAtTime(time).position)
                    .scaledToMagnitude(RlConstants.BALL_RADIUS * 1.8)
                    .rotate(Vector3.UP_VECTOR.scaled(Math.PI*time * leftOrRight))
                    .plus(RawBallTrajectory.ballAtTime(time).position);
        })
        .remove(movingPoint -> movingPoint.physicsState.offset.minus(input.car.position).magnitude() < 100)
        .remove(movingPoint -> movingPoint.time < 0.1)
        .first(5, RlConstants.BOT_REFRESH_TIME_PERIOD);

        double distanceFromBall = destination.physicsState.offset
                .distance(input.car.position);
        double desiredSpeed = distanceFromBall/destination.time;
        //System.out.println(destination.time);
        double accelerationToReach = (desiredSpeed - input.car.velocity.magnitude())/destination.time;
        boolean isBoosting = accelerationToReach > MaxAccelerationFromThrottleFinder.compute(input.car.velocity.magnitude())
                && input.car.velocity.magnitude() < 2290;
        bot.output().boost(isBoosting);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(destination.physicsState.offset);
        groundOrientationController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        double ballSpeed = input.ball.velocity.magnitude();
        double ballSpeedFromCarSpeedTowardCar = input.ball.velocity
                .dotProduct(input.car.position
                        .minus(input.ball.position)
                        .normalized());
        if(ballSpeedFromCarSpeedTowardCar >= 0) {
            bot.output().boost(false);
            return new GiveItANudgeState(bot, masterDribbleController);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("swipe not wipe", Color.YELLOW, input.car.position.toFlatVector(), 1, 1);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        if(destination != null) {
            shapeRenderer.renderCross(destination.physicsState.offset, Color.magenta);
        }
    }
}
