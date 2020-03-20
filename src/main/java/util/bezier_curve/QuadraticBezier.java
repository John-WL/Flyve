package util.bezier_curve;

import util.vector.Vector3;

import java.util.List;

import static util.math.CalculusApproximation.GAUSSIAN_CONSTANT_WI;
import static util.math.CalculusApproximation.GAUSSIAN_CONSTANT_XI;

public class QuadraticBezier extends BezierCurve implements CurveSegment {

    public QuadraticBezier(Vector3 p0, Vector3 p1, Vector3 p2) {
        super(p0, p1, p2);
    }

    @Override
    public Vector3 interpolate(double t) {
        List<Vector3> points = getPoints();
        double u = 1-t;

        return points.get(0).scaled(u*u) .plus(
                points.get(1).scaled(2*t*u)) .plus(
                points.get(2).scaled(t*t));
    }

    @Override
    public Vector3 derivative(double t) {
        return getPoints().get(0).scaled(2*(t-1)) .plus(
                getPoints().get(1).scaled(2*(1-(2*t)))) .plus(
                getPoints().get(2).scaled(2*t));
    }

    @Override
    public Vector3 integral(double a, double b) {
        // doing a gaussian quadrature with 2 points...

        double c0 = (b-a)/2;
        double c1 = (b+a)/2;

        Vector3 firstConstantDerivative = derivative(c0*GAUSSIAN_CONSTANT_XI[0] + c1);
        Vector3 secondConstantDerivative = derivative(c0*GAUSSIAN_CONSTANT_XI[1] + c1);

        return new Vector3(
            c0 * (
                    GAUSSIAN_CONSTANT_WI[0]*Math.sqrt(1+(firstConstantDerivative.x*firstConstantDerivative.x)) +
                            GAUSSIAN_CONSTANT_WI[1]*Math.sqrt(1+(secondConstantDerivative.x*secondConstantDerivative.x))
            ),
            c0 * (
                    GAUSSIAN_CONSTANT_WI[0]*Math.sqrt(1+(firstConstantDerivative.y*firstConstantDerivative.y)) +
                            GAUSSIAN_CONSTANT_WI[1]*Math.sqrt(1+(secondConstantDerivative.y*secondConstantDerivative.y))
            ),
            c0 * (
                    GAUSSIAN_CONSTANT_WI[0]*Math.sqrt(1+(firstConstantDerivative.z*firstConstantDerivative.z)) +
                            GAUSSIAN_CONSTANT_WI[1]*Math.sqrt(1+(secondConstantDerivative.z*secondConstantDerivative.z))
            )
        );
    }
}
