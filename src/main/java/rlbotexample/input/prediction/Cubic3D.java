package rlbotexample.input.prediction;

import util.math.vector.Vector3;

public class Cubic3D implements Trajectory3D {

    private final Vector3 initialPosition;
    private final Vector3 initialVelocity;
    private final Vector3 acceleration;
    private final Vector3 accelerationChange;

    public Cubic3D(Vector3 initialPosition, Vector3 initialVelocity, Vector3 acceleration, Vector3 accelerationChange) {
        this.initialPosition = initialPosition;
        this.initialVelocity = initialVelocity;
        this.acceleration = acceleration;
        this.accelerationChange = accelerationChange;
    }

    @Override
    public Vector3 apply(Double deltaTime) {
        final Vector3 deltaVelocity = initialVelocity.scaled(deltaTime);
        final Vector3 deltaDeltaAcceleration = acceleration.scaled(deltaTime * deltaTime / 2);
        final Vector3 deltaDeltaDeltaAccelerationChange = accelerationChange.scaled(deltaTime * deltaTime * deltaTime / 6);

        return initialPosition.plus(deltaVelocity).plus(deltaDeltaAcceleration).plus(deltaDeltaDeltaAccelerationChange);
    }

    public Vector3 derivative(double deltaTime) {
        final Vector3 deltaAcceleration = acceleration.scaled(deltaTime);
        final Vector3 deltaDeltaAccelerationChange = accelerationChange.scaled(deltaTime*deltaTime/2);

        return initialVelocity.plus(deltaAcceleration).plus(deltaDeltaAccelerationChange);
    }
}
