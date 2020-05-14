package rlbotexample.input.prediction;

import util.vector.Vector3;

public class Parabola3D {

    private final Vector3 initialPosition;
    private final Vector3 initialVelocity;
    private final Vector3 acceleration;
    private double airDragCoefficient;

    public Parabola3D(Vector3 initialPosition, Vector3 initialVelocity, Vector3 acceleration) {
        this.initialPosition = initialPosition;
        this.initialVelocity = initialVelocity;
        this.acceleration = acceleration;
        this.airDragCoefficient = 1;
    }

    public void setAirDrag(double airDragCoefficient) {
        this.airDragCoefficient = airDragCoefficient;
    }

    public Vector3 compute(double deltaTime) {
        Vector3 deltaVelocity = initialVelocity.scaled(deltaTime * airDragCoefficient);
        double accelerationFactor = deltaTime * deltaTime / 2;
        Vector3 deltaDeltaAcceleration = acceleration.scaled(accelerationFactor);

        return initialPosition.plus(deltaVelocity.plus(deltaDeltaAcceleration));
    }

    public Vector3 derivative(double deltaTime) {
        Vector3 deltaAcceleration = acceleration.scaled(deltaTime);
        Vector3 newVelocity = initialVelocity.minus(initialVelocity.scaled((1-airDragCoefficient)*deltaTime));
        return newVelocity.plus(deltaAcceleration);
    }
}
