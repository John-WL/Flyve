package rlbotexample.input.prediction.gamestate_prediction.object_collisions;

import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import util.game_constants.RlConstants;
import util.math.linear_transform.LinearApproximator;
import util.math.matrix.Matrix3By3;
import util.math.vector.Vector;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class BallCollisionWithCar4 {

    final BallData initialBallData;
    final CarData initialCarData;
    final Vector3 contactPoint;
    final LinearApproximator s;

    public BallCollisionWithCar4(final BallData ballData, final CarData carData) {
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
                > RlConstants.BALL_RADIUS * RlConstants.BALL_RADIUS) {
            return initialBallData;
        }

        // delta V computation
        Vector3 vc = initialCarData.velocity;
        Vector3 wc = initialCarData.spin;
        Matrix3By3 Lc = contactPoint.minus(initialCarData.position).asUnitMatrix();

        Vector3 vb = initialBallData.velocity;
        Vector3 wb = initialBallData.spin;
        Matrix3By3 Lb = contactPoint.minus(initialBallData.position).asUnitMatrix();

        final Vector3 deltaV = vc.minus(Lc.multiply(wc))
                .minus(vb.minus(Lb.multiply(wb)));

        // mass thingy computation
        double mc = RlConstants.CAR_MASS;
        double mb = RlConstants.BALL_MASS;

        double IbNoVec = 0.4 * mb * RlConstants.BALL_RADIUS * RlConstants.BALL_RADIUS;
        Matrix3By3 Ic = new Matrix3By3(
                new Vector3(751.0, 0.0, 0.0),
                new Vector3(0.0, 1334.0, 0.0),
                new Vector3(0.0, 0.0, 1836.0));
        Matrix3By3 Ib = Matrix3By3.UNIT.scaled(IbNoVec);

        final Matrix3By3 M = (Matrix3By3.UNIT.scaled((1/mb) + (1/mc))
                .minus(Lc.multiply(Ic.inverse()).multiply(Lc))
                .minus(Lb.multiply(Ib.inverse()).multiply(Lb)))
                .inverse();

        // theoretical impulse J computation
        Vector3 J = M.multiply(deltaV)
                .scaled(-1);

        // clamping J
        double mu = 0.285;
        Vector3 nb = contactPoint.minus(initialCarData.position).normalized();
        final Vector3 J_perp = nb.scaledToMagnitude(J.dotProduct(nb));
        final Vector3 J_paraUnscaled = J.minus(J_perp);
        final Vector3 J_para = J_paraUnscaled.magnitude() <= J_perp.magnitude()*mu ? J_paraUnscaled : J_perp.scaled(mu);

        J = J_perp.plus(J_para);

        // finding weird actual impulse J
        Vector3 f = initialCarData.hitBox.frontOrientation;
        Vector3 xc = initialCarData.position;
        Vector3 xb = initialBallData.position;
        Vector3 nWeird = xb.minus(xc);
        nWeird = nWeird.scaled(1, 1, 0.35);
        nWeird = (nWeird.minus(f.scaled(nWeird.dotProduct(f)*0.35))).normalized();
        double dv = vb.minus(vc).magnitude();
        final Vector3 J_mod = nWeird.scaled(RlConstants.BALL_MASS * dv * s.compute(dv));

        // new velocity and spin
        Vector3 newVelocity = vb.plus(J.plus(J_mod).scaled(1/mb));
        Vector3 newSpin = wb.plus(Ib.inverse().multiply(Lb).multiply(J));

        // capping values
        if(newVelocity.magnitude() > RlConstants.BALL_MAX_SPEED) {
            newVelocity = newVelocity.scaledToMagnitude(RlConstants.BALL_MAX_SPEED);
        }
        if(newSpin.magnitude() > RlConstants.BALL_MAX_SPIN) {
            newSpin = newSpin.scaledToMagnitude(RlConstants.BALL_MAX_SPIN);
        }

        return new BallData(initialBallData.position, newVelocity, newSpin, 0);
    }
}
