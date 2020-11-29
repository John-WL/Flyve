package rlbotexample.input.prediction.gamestate_prediction.object_collisions;

import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import util.game_constants.RlConstants;
import util.math.LinearApproximator;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class BallCollisionWithCar {

    final BallData initialBallData;
    final CarData initialCarData;
    final Vector3 contactPoint;
    final LinearApproximator s;

    public BallCollisionWithCar(final BallData ballData, final CarData carData, Vector3 contactPoint) {
        this.initialBallData = ballData;
        this.initialCarData = carData;
        this.contactPoint = contactPoint;
        this.s = new LinearApproximator();
        s.sample(new Vector2(0, 0.65));
        s.sample(new Vector2(600, 0.65));
        s.sample(new Vector2(2300, 0.56));
        s.sample(new Vector2(4600, 0.3));
    }

    public BallData compute(final double deltaTime) {
        // delta V computation
        Vector3 vc = initialCarData.velocity;
        Vector3 wc = initialCarData.spin;
        Vector3 Lc = contactPoint.minus(initialCarData.position);

        Vector3 vb = initialBallData.velocity;
        Vector3 wb = initialBallData.spin;
        Vector3 Lb = contactPoint.minus(initialBallData.position);

        final Vector3 deltaV = vc.minus(wc.crossProduct(Lc))
                .minus(vb.minus(wb.crossProduct(Lb)));

        // mass thingy computation
        Vector3 mc = new Vector3(180, 180, 180);
        Vector3 mb = new Vector3(30, 30, 30);

        double IbNoVec = 0.4 * mb.x * RlConstants.BALL_RADIUS * RlConstants.BALL_RADIUS;
        //double IbNoVec = 1/12.0;
        Vector3 Ic = new Vector3(1/36.07956616966136, 1/12.14599781908070, 1/8.91962804287785);
        //Vector3 Ic = new Vector3(IbNoVec, IbNoVec, IbNoVec);
        Vector3 Ib = new Vector3(IbNoVec, IbNoVec, IbNoVec);

        final Vector3 M = mb.inverse().plus(mc.inverse())
                .minus(Lc.crossProduct(Ic.inverse())).crossProduct(Lc)
                .minus(Lb.crossProduct(Ib.inverse())).crossProduct(Lb)
                .inverse();

        // theoretical impulse J computation
        Vector3 J = M.scaled(deltaV)
                .scaled(-1);

        // clamping J
        double mu = 0.285;
        Vector3 nb = Lb.normalized();
        final Vector3 J_perp = nb.scaledToMagnitude(J.dotProduct(nb));
        final Vector3 J_paraUnscaled = J.minus(J_perp);
        final Vector3 J_para = J_paraUnscaled.magnitude() <= J_perp.magnitude()*mu ? J_paraUnscaled : J_perp.scaled(mu);

        J = J_perp.plus(J_para);

        // finding weird actual impulse J
        Vector3 f = initialCarData.hitBox.frontOrientation;
        Vector3 xc = initialCarData.position;
        Vector3 xb = initialBallData.position;
        Vector3 n = xb.minus(xc);
        n = n.scaled(1, 1, 0.35);
        n = n.minus(f.scaled(n.dotProduct(f)*0.35)).normalized();
        double dv = vb.minus(vc).magnitude();
        final Vector3 J_mod = n.scaled(RlConstants.BALL_MASS * dv * s.compute(dv));

        // new velocity and spin
        final Vector3 newVelocity = vb.plus(mb.inverse().crossProduct(J)).plus(mb.inverse().scaled(J_mod));
        final Vector3 newSpin = initialBallData.spin.plus(Ib.inverse().crossProduct(Lb.crossProduct(J)));

        return new BallData(initialBallData.position, newVelocity, newSpin, 0);
    }
}
