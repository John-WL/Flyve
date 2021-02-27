package rlbotexample.input.prediction.gamestate_prediction.object_collisions;

import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import util.game_constants.RlConstants;
import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class BallCollisionWithCar2 {

    final BallData initialBallData;
    final CarData initialCarData;
    final Vector3 contactPoint;
    final LinearApproximator s;

    public BallCollisionWithCar2(final BallData ballData, final CarData carData) {
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
        // checking for intersection within ball radius
        // if the contact point is not within the ball radius,
        // then we're not gonna compute the collision.
        // we're also avoiding to compute the collision if
        // the ball is going away from the car. This way,
        // we avoid the scenario where the ball was hit the previous frame,
        // but didn't travel far enough to get out of the car's hit box.
        if(initialBallData.position.minus(contactPoint).magnitudeSquared()
                > RlConstants.BALL_RADIUS * RlConstants.BALL_RADIUS
                || initialBallData.velocity.minus(initialCarData.velocity)
                    .dotProduct(initialBallData.position.minus(initialCarData.position))
                > 0
                || true) {
            return initialBallData;
        }

        // some dumb variable names (sorry)
        Vector3 carSurfaceVelocity = initialCarData.velocity
                .plus(initialCarData.spin
                        .crossProduct(contactPoint.minus(initialCarData.position)));
        Vector3 ballSurfaceVelocity = initialBallData.velocity
                .plus(initialBallData.spin
                        .crossProduct(contactPoint.minus(initialBallData.position)));

        // find the normal the engine is using "natively"
        Vector3 nEngine = initialBallData.position.minus(contactPoint).normalized();
        Vector3 newVelocityWithoutSpinPerp = ballSurfaceVelocity.projectOnto(nEngine).scaled(-0.6);
        Vector3 newVelocityWithoutSpinPara = ballSurfaceVelocity.minus(ballSurfaceVelocity.projectOnto(nEngine));
        Vector3 newVelocityWithoutSpin = newVelocityWithoutSpinPerp.plus(newVelocityWithoutSpinPara);

        // find the weird impulse J
        Vector3 f = initialCarData.hitBox.frontOrientation;
        Vector3 xc = initialCarData.position;
        Vector3 xb = initialBallData.position;
        Vector3 nWeird = xb.minus(xc);
        nWeird = nWeird.scaled(1, 1, 0.35);
        nWeird = (nWeird.minus(f.scaled(nWeird.dotProduct(f)*0.35))).normalized();
        double dv = ballSurfaceVelocity.minus(carSurfaceVelocity).magnitude();
        final Vector3 J_mod = nWeird.scaled(RlConstants.BALL_MASS * dv * s.compute(dv));

        // new velocity and spin
        Vector3 mb = new Vector3(RlConstants.BALL_MASS, RlConstants.BALL_MASS, RlConstants.BALL_MASS);
        final Vector3 newVelocity = newVelocityWithoutSpin
                .plus(J_mod);

        return new BallData(initialBallData.position, newVelocity, initialBallData.spin, 0);
    }
}
