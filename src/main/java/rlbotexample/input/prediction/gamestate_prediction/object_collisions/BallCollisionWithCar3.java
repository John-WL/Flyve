package rlbotexample.input.prediction.gamestate_prediction.object_collisions;

import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import util.game_constants.RlConstants;
import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class BallCollisionWithCar3 {

    private static final double FRICTION_COEFFICIENT = 2;

    final BallData initialBallData;
    final CarData initialCarData;
    final Vector3 contactPoint;
    final LinearApproximator s;

    public BallCollisionWithCar3(final BallData ballData, final CarData carData) {
        this.initialBallData = ballData;
        this.initialCarData = carData;
        this.contactPoint = carData.hitBox.closestPointOnSurface(ballData.position);
        this.s = new LinearApproximator();
        s.sample(new Vector2(0, 0.65));
        s.sample(new Vector2(600, 0.65));
        s.sample(new Vector2(2300, 0.56));
        s.sample(new Vector2(4600, 0.3));
    }

    public BallData compute() {
        // no collision? no calculations
        if(initialBallData.position.minus(contactPoint).magnitudeSquared()
                > RlConstants.BALL_RADIUS * RlConstants.BALL_RADIUS
                || initialBallData.velocity.minus(initialCarData.velocity)
                .dotProduct(initialBallData.position.minus(initialCarData.position))
                > 0) {
            return initialBallData;
        }

        // calculate the difference in speed at contact point (I think that the impulse is proportional to this)
        Vector3 carSurfaceVelocity = initialCarData.velocity
                .plus(initialCarData.spin.crossProduct(contactPoint.minus(initialCarData.position)));
        Vector3 ballSurfaceVelocity = initialBallData.velocity
                .plus(initialBallData.spin.crossProduct(contactPoint.minus(initialBallData.position)));
        Vector3 deltaVelocity = ballSurfaceVelocity.minus(carSurfaceVelocity);

        // calculate the reduced mass vector (I think this is the scalar of the difference in speed)
        double reducedMass = 1/(1/RlConstants.BALL_MASS + 1/RlConstants.CAR_MASS);
                // - the car inertia
                // - the ball inertia
        // the thing is, only god knows how the inertia is represented and calculated...

        // calculate the impulse J
        Vector3 J = deltaVelocity.scaled(-reducedMass);
        J = adjustJ(J);

        final Vector3 newVelocity = initialBallData.velocity
                .plus(J
                        .scaled(1/RlConstants.BALL_MASS));
        final Vector3 newSpin = initialBallData.spin
                .plus(contactPoint.minus(initialBallData.position).crossProduct(J)
                        .scaled(1/RlConstants.BALL_INERTIA));

        return new BallData(initialBallData.position, newVelocity, newSpin, 0);
    }

    private Vector3 adjustJ(Vector3 J) {
        final Vector3 normal = contactPoint.minus(initialBallData.position).normalized();
        final Vector3 J_perpendicular = J.projectOnto(normal);
        Vector3 J_parallel = J.minus(J_perpendicular);
        if(J_parallel.magnitudeSquared() > J_perpendicular.scaled(FRICTION_COEFFICIENT).magnitudeSquared()) {
            J_parallel = J_parallel.scaledToMagnitude(J_perpendicular.scaled(FRICTION_COEFFICIENT).magnitude());
        }
        return J_parallel.plus(J_perpendicular);
    }
}
