package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Flip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.MiddleJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GroundDirectionalHit7 extends SkillController {

    private BotBehaviour botBehaviour;

    private Vector3 ballDestination;

    private GroundOrientationController2 groundOrientationController;
    private JumpController jumpController;

    public GroundDirectionalHit7(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();
        this.groundOrientationController = new GroundOrientationController2(bot);
        this.jumpController = new JumpController(bot);
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Trajectory3D stupidCarTrajectory = t ->
                input.car.position.plus(
                        input.car.orientation.noseVector.scaled(displacementFromThrottleAccelerationNoBoost(
                                input.car.velocity.magnitude(),
                                t
                        ))
                );
        Trajectory3D ballTrajectory = input.statePrediction.ballAsTrajectory().modify(m -> {
            Vector3 ballPosition = m.physicsState.offset;
            Vector3 ballOffset = ballPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS+30);
            return ballPosition.plus(ballOffset);
        });

        double timeBeforeHittingBall = Trajectory3D.findTimeOfClosestApproach(stupidCarTrajectory, ballTrajectory, 5, RlConstants.BOT_REFRESH_RATE);

        groundOrientationController.setDestination(ballTrajectory.apply(timeBeforeHittingBall));
        groundOrientationController.updateOutput(input);

        botBehaviour.output().throttle(1);

        if(input.ball.position.minus(input.car.position).magnitude() < 700
                && input.car.velocity.normalized().dotProduct(ballTrajectory.apply(timeBeforeHittingBall).minus(input.car.position).normalized()) > 0.9) {
            jumpController.setFirstJumpType(new MiddleJump(), input);
            jumpController.setSecondJumpType(new Flip(), input);
        }
        else {
            jumpController.setFirstJumpType(new Wait(), input);
            jumpController.setSecondJumpType(new Wait(), input);
        }
        jumpController.setJumpDestination(input.ball.position);
        jumpController.updateOutput(input);
    }

    private double displacementFromThrottleAccelerationNoBoost(double vi, double t) {
        if(vi > 0) {
            double timeWhenHittingMaxSpeed = timeWhenHittingMaxSpeedFromThrottleAccelerationNoBoost(vi);
            if (t < timeWhenHittingMaxSpeed) {
                double k = (36 / 35.0) * (11520 / 7.0 - vi);
                return k * Math.pow(Math.E, -36 * t / 35.0) + (11520 * t / 7.0) - k;
            }

            if (vi > 1410) {
                return vi * t;
            }

            double k = (36 / 35.0) * (11520 / 7.0 - vi);
            double initialDisplacement = k * Math.pow(Math.E, -36 * timeWhenHittingMaxSpeed / 35.0) + (11520 * timeWhenHittingMaxSpeed / 7.0) - k;

            return initialDisplacement + (1410 * (t - timeWhenHittingMaxSpeed));
        }

        double timeWhenPassingFromNegativeSpeedToPositiveSpeed = timeWhenPassingFromNegativeSpeedToPositiveSpeedFromThrottleAccelerationNoBoost(vi);

        if(t < timeWhenPassingFromNegativeSpeedToPositiveSpeed) {
            return 1750*t*t + vi*t;
        }

        double vi2 = 0;
        double t2 = t - timeWhenPassingFromNegativeSpeedToPositiveSpeed;
        double xi2 = 1750 * timeWhenPassingFromNegativeSpeedToPositiveSpeed * timeWhenPassingFromNegativeSpeedToPositiveSpeed
                + vi*timeWhenPassingFromNegativeSpeedToPositiveSpeed;

        double timeWhenHittingMaxSpeed = timeWhenHittingMaxSpeedFromThrottleAccelerationNoBoost(vi2);
        if (t2 < timeWhenHittingMaxSpeed) {
            double k = (36 / 35.0) * (11520 / 7.0 - vi2);
            return k * Math.pow(Math.E, -36 * t2 / 35.0) + (11520 * t2 / 7.0) - k + xi2;
        }

        double k = (36 / 35.0) * (11520 / 7.0 - vi2);
        double initialDisplacement = k * Math.pow(Math.E, -36 * timeWhenHittingMaxSpeed / 35.0) + (11520 * timeWhenHittingMaxSpeed / 7.0) - k + xi2;

        return initialDisplacement + (1410 * (t2 - timeWhenHittingMaxSpeed));
    }

    private double timeWhenHittingMaxSpeedFromThrottleAccelerationNoBoost(double vi) {
        double k = 11520/7.0;
        return Math.log((1410 - k) / (vi - k)) * (-35/36.0);
    }

    private double timeWhenPassingFromNegativeSpeedToPositiveSpeedFromThrottleAccelerationNoBoost(double vi) {
        return -vi / 3500;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {

    }
}
