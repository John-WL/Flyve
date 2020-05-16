package rlbotexample.input.prediction;

import rlbotexample.input.dynamic_data.BallData;
import util.game_constants.RlConstants;
import util.vector.Vector3;

public class BallBounce {

    private final Vector3 initialPosition;
    private final Vector3 initialVelocity;
    private final Vector3 spin;
    private final Vector3 surfaceNormal;
    private final Vector3 surfaceVelocity;
    private final Vector3 velocityComponentParallelToHitNormal;
    private final Vector3 velocityComponentPerpendicularToHitNormal;

    public BallBounce(final BallData ballData, final Vector3 hitNormal) {
        this.initialPosition = ballData.position;
        this.initialVelocity = ballData.velocity;
        this.spin = ballData.spin;
        this.surfaceNormal = hitNormal;
        this.surfaceVelocity = ballData.surfaceVelocity(hitNormal);
        this.velocityComponentParallelToHitNormal = initialVelocity.projectOnto(hitNormal);
        this.velocityComponentPerpendicularToHitNormal = initialVelocity.minus(velocityComponentParallelToHitNormal);
    }

    public BallData compute() {

        // WORKING CODE (but not when wall sliding upward from the ground)
        final Vector3 slipSpeed = velocityComponentPerpendicularToHitNormal.minus(surfaceVelocity);
        final double surfaceSpeedRatio = velocityComponentParallelToHitNormal.magnitude()/slipSpeed.magnitude();

        final Vector3 newParallelVelocity = velocityComponentParallelToHitNormal.scaled(-0.6);
        final Vector3 perpendicularDeltaVelocity = slipSpeed.scaled(-Math.min(1.0, 2*surfaceSpeedRatio) * 0.285);

        Vector3 newVelocity = newParallelVelocity.plus(velocityComponentPerpendicularToHitNormal.plus(perpendicularDeltaVelocity));

        final Vector3 deltaSpin = perpendicularDeltaVelocity.crossProduct(surfaceNormal).scaled(0.0003 * RlConstants.BALL_RADIUS);
        final Vector3 newSpin = spin.plus(deltaSpin);

        return new BallData(initialPosition, newVelocity, newSpin, 0);
    }
}
