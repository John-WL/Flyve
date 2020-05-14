package rlbotexample.input.prediction;

import rlbotexample.input.dynamic_data.BallData;
import util.game_constants.RlConstants;
import util.vector.Vector3;

public class BallBounce {

    private static final double VELOCITY_DECREASE_COEFFICIENT_FOR_SPEED_TANGENT_TO_HIT_NORMAL = 2.5;

    private final Vector3 initialPosition;
    private final Vector3 initialVelocity;
    private final Vector3 spin;
    private final Vector3 surfaceNormal;
    private final Vector3 surfaceVelocity;
    private final Vector3 velocityComponentInHitNormalDirection;
    private final Vector3 velocityComponentTangentToHitNormalDirection;
    private final double angleOfIncidence;

    public BallBounce(final BallData ballData, final Vector3 hitNormal) {
        this.initialPosition = ballData.position;
        this.initialVelocity = ballData.velocity;
        this.spin = ballData.spin;
        this.surfaceNormal = hitNormal;
        this.surfaceVelocity = ballData.surfaceVelocity(hitNormal);
        this.velocityComponentInHitNormalDirection = initialVelocity.projectionOnto(hitNormal);
        this.velocityComponentTangentToHitNormalDirection = initialVelocity.minus(velocityComponentInHitNormalDirection);
        this.angleOfIncidence = initialVelocity.angleWith(hitNormal);
    }

    public BallData compute() {
        final Vector3 velocityDifferenceBetweenVelocityAndSurfaceVelocity = surfaceVelocity.minus(initialVelocity);
        final Vector3 deltaVelocity = velocityDifferenceBetweenVelocityAndSurfaceVelocity.scaled(Math.sin(angleOfIncidence)/VELOCITY_DECREASE_COEFFICIENT_FOR_SPEED_TANGENT_TO_HIT_NORMAL);
        final Vector3 newVelocityTangentToHitNormal = velocityComponentTangentToHitNormalDirection.minus(deltaVelocity);
        final Vector3 newVelocity = newVelocityTangentToHitNormal.plus(velocityComponentInHitNormalDirection.scaled(-0.6));

        final double deltaSpinStrength = -velocityComponentInHitNormalDirection.magnitude();
        final Vector3 deltaSpin = surfaceNormal.crossProduct(velocityDifferenceBetweenVelocityAndSurfaceVelocity.scaled(deltaSpinStrength));
        final Vector3 newSpin = spin.plus(deltaSpin);

        return new BallData(initialPosition, newVelocity, newSpin, 0);
    }
}
