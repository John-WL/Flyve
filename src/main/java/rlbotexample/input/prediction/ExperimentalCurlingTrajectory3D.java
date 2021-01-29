package rlbotexample.input.prediction;

import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

public class ExperimentalCurlingTrajectory3D implements Trajectory3D {

    private final Vector3 initialPosition;
    private final Vector3 initialVelocity;
    private final Vector3 acceleration;
    private final Vector3 angularMomentum;

    public ExperimentalCurlingTrajectory3D(Vector3 initialPosition, Vector3 initialVelocity, Vector3 acceleration, Vector3 angularMomentum) {
        this.initialPosition = initialPosition;
        this.initialVelocity = initialVelocity;
        this.acceleration = acceleration;
        this.angularMomentum = angularMomentum;
    }

    public ExperimentalCurlingTrajectory3D(ExtendedCarData carData) {
        this.initialPosition = carData.position;
        this.initialVelocity = carData.velocity;
        this.acceleration = carData.orientation.noseVector.scaled(RlConstants.ACCELERATION_DUE_TO_BOOST);
        this.angularMomentum = carData.spin;
    }

    public Vector3 compute(double time) {
        final Vector3 angularMomentum;
        if(this.angularMomentum.magnitude() < 0.02) {
            angularMomentum = acceleration;
        }
        else {
            angularMomentum = this.angularMomentum;
        }

        double distanceScalar = 1/angularMomentum.magnitudeSquared();
        double velocityScalar = 1/angularMomentum.magnitude();
        Trajectory3D localRotatingBody =
                t -> acceleration
                        .rotate(angularMomentum.scaled(t))
                        .rotate(angularMomentum.scaledToMagnitude(Math.PI))
                        .scaled(distanceScalar);
        Trajectory3D localRotatingBodyVelocity =
                t -> acceleration
                        .rotate(angularMomentum.scaled(t))
                        .rotate(angularMomentum.scaledToMagnitude(Math.PI/2))
                        .scaled(velocityScalar);

        //Vector3 lrb0 = localRotatingBody.compute(0);
        Vector3 lrb0 = acceleration.rotate(angularMomentum.scaledToMagnitude(Math.PI)).scaled(distanceScalar);
        //Vector3 dlrb0 = localRotatingBodyVelocity.compute(0);
        Vector3 dlrb0 = acceleration.rotate(angularMomentum.scaledToMagnitude(Math.PI/2)).scaled(velocityScalar);
        Vector3 constantAcceleration = acceleration.projectOnto(angularMomentum);

        Vector3 linearPosition = new Parabola3D(initialPosition.minus(lrb0), initialVelocity.minus(dlrb0), constantAcceleration.plus(RlConstants.GRAVITY_VECTOR)).compute(time);

        return linearPosition.plus(localRotatingBody.compute(-time));
    }

    public Vector3 derivative(double time) {
        final Vector3 deltaAcceleration = acceleration.toQuaternion().multiply(angularMomentum.toQuaternion().exp()).toVector3();
        final Vector3 newVelocity = initialVelocity.minus(initialVelocity.scaled(time));
        return newVelocity.plus(deltaAcceleration);
    }
}
