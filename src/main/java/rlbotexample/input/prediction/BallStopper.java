package rlbotexample.input.prediction;

import rlbotexample.input.dynamic_data.BallData;
import util.game_constants.RlConstants;
import util.vector.Vector3;

public class BallStopper {

    private static final double APPROXIMATED_SPIN_ACCELERATION_COEFFICIENT_DUE_TO_NON_NULL_VELOCITY_BETWEEN_BALL_SURFACE_AND_MAP = Math.PI/2000;

    private double amountOfTimeSinceCriticalSlowSpeedReached;

    public BallStopper() {
        this.amountOfTimeSinceCriticalSlowSpeedReached = 0;
    }

    public BallData compute(BallData ballData, double deltaTime) {
        /*
        this.initialPosition = ballData.position;
        this.initialVelocity = ballData.velocity;
        this.spin = ballData.spin;
        this.surfaceNormal = surfaceNormal;
        this.surfaceVelocity = spin.crossProduct(surfaceNormal).scaled(RlConstants.BALL_RADIUS);
        this.amountOfTimeSinceCriticalSlowSpeedReached += deltaTime;

        Vector3 deltaVelocity;
        Vector3 newSpin = spin;
        if(initialVelocity.magnitudeSquared() > speedThreshold * speedThreshold) {
            double deltaFrictionMagnitude = -fastSpeedSurfaceFrictionForce * deltaTime;
            Vector3 deltaFriction = initialVelocity.scaledToMagnitude(deltaFrictionMagnitude);
            deltaVelocity = initialVelocity.scaled(deltaTime * airDragCoefficient).plus(deltaFriction);

            Vector3 spinSurfaceAcceleration = initialVelocity.plus(surfaceVelocity).scaled(APPROXIMATED_SPIN_ACCELERATION_COEFFICIENT_DUE_TO_NON_NULL_VELOCITY_BETWEEN_BALL_SURFACE_AND_MAP);
            Vector3 spinAcceleration = surfaceNormal.crossProduct(spinSurfaceAcceleration);
            Vector3 deltaSpinAcceleration = spinAcceleration.scaled(deltaTime);
            newSpin = spin.plus(deltaSpinAcceleration);
            if(newSpin.magnitudeSquared() > square(RlConstants.BALL_MAX_SPIN)) {
                newSpin.scaledToMagnitude(RlConstants.BALL_MAX_SPIN);
            }
        }
        else {
            deltaVelocity = initialVelocity.scaled(deltaTime * slowSpeedSurfaceFrictionCoefficient);
        }

        Vector3 newPosition = initialPosition.plus(deltaVelocity);
        Vector3 newVelocity = deltaVelocity.scaled(1/deltaTime);
        BallData newBallData = new BallData(newPosition, newVelocity, newSpin, 0);*/

        amountOfTimeSinceCriticalSlowSpeedReached += deltaTime;

        Vector3 newPosition = ballData.position;
        Vector3 newVelocity = ballData.velocity;
        Vector3 newSpin = ballData.spin;

        if(hasStoppedMoving(ballData)) {
            amountOfTimeSinceCriticalSlowSpeedReached = 0;
            newVelocity = new Vector3();
            newSpin = new Vector3();
        }

        return new BallData(newPosition, newVelocity, newSpin, 0);
    }

    private boolean hasStoppedMoving(BallData ballData) {
        return ballData.velocity.magnitudeSquared() < square(RlConstants.BALL_MINIMUM_ROLLING_SPEED)
                && amountOfTimeSinceCriticalSlowSpeedReached > RlConstants.BALL_CRITICAL_AMOUNT_OF_TIME_OF_SLOW_SPEED_ROLLING_BEFORE_COMPLETE_STOP
                && ballData.spin.scaled(1/(Math.PI*2)).magnitudeSquared() < square(RlConstants.BALL_MINIMUM_RPM_WHEN_ROLLING_BEFORE_COMPLETE_STOP/60);
    }

    private double square(double x) {
        return x*x;
    }
}
