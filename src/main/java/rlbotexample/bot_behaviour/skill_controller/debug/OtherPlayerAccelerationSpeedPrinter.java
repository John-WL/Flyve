package rlbotexample.bot_behaviour.skill_controller.debug;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.slope_samples.MaxAccelerationFromThrottleFinder;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class OtherPlayerAccelerationSpeedPrinter extends SkillController {

    private double timeOffset;

    public OtherPlayerAccelerationSpeedPrinter() {
        super();

        timeOffset = 0;
    }

    @Override
    public void updateOutput(DataPacket input) {
    }

    private double speedFromThrottleAccelerationNoBoost(double vi, double t) {
        if(vi > 0) {
            if (t < timeWhenHittingMaxSpeedFromThrottleAccelerationNoBoost(vi)) {
                double k = 11520 / 7.0;
                return (vi - k) * Math.pow(Math.E, -36 * t / 35) + k;
            }

            if (vi > 1410) {
                return vi;
            }

            return 1410;
        }

        double timeWhenPassingFromNegativeSpeedToPositiveSpeed = timeWhenPassingFromNegativeSpeedToPositiveSpeedFromThrottleAccelerationNoBoost(vi);

        if(t < timeWhenPassingFromNegativeSpeedToPositiveSpeed) {
            return 3500 * t + vi;
        }

        double vi2 = 0;
        double t2 = t - timeWhenPassingFromNegativeSpeedToPositiveSpeed;

        if (t2 < timeWhenHittingMaxSpeedFromThrottleAccelerationNoBoost(vi)) {
            double k = 11520 / 7.0;
            return (vi2 - k) * Math.pow(Math.E, -36 * t2 / 35) + k;
        }

        return 1410;
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
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        System.out.println(
                displacementFromThrottleAccelerationNoBoost(
                        input.allCars.get(1-input.playerIndex).velocity.magnitude(),
                        1)
        );

        timeOffset += 1/RlConstants.BOT_REFRESH_RATE;
        if(timeOffset > 1) {
            timeOffset = 0.5;
        }

        for(double i = 0; i < 3; i += 0.5) {
            new ShapeRenderer(renderer)
                    .renderCross(
                            input.allCars.get(1 - input.playerIndex).position.plus(
                                    input.allCars.get(1 - input.playerIndex).orientation.noseVector.scaledToMagnitude(
                                    displacementFromThrottleAccelerationNoBoost(
                                            input.allCars.get(1-input.playerIndex).velocity.dotProduct(input.allCars.get(1-input.playerIndex).orientation.noseVector),
                                            i + (1 - timeOffset)))),
                            Color.CYAN);
        }
    }
}
