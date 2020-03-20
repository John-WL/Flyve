package util.bezier_curve;

import util.math.CalculusApproximation;
import util.math.Function;
import util.vector.Vector3;

import java.util.*;

public class BezierCurve implements CurveSegment {

    private static final double DERIVATIVE_PRECISION = 0.0001;

    private List<Vector3> points;

    private Function selfDerivativeX;
    private Function selfDerivativeY;
    private Function selfDerivativeZ;

    public BezierCurve(Vector3... points)
    {
        this(Arrays.asList(points));
    }

    public BezierCurve(List<Vector3> points) {
        this.points = new ArrayList<>();
        this.points.addAll(points);

        selfDerivativeX = CalculusApproximation.derivative((double t) -> interpolate(t).x, DERIVATIVE_PRECISION);
        selfDerivativeY = CalculusApproximation.derivative((double t) -> interpolate(t).y, DERIVATIVE_PRECISION);
        selfDerivativeZ = CalculusApproximation.derivative((double t) -> interpolate(t).z, DERIVATIVE_PRECISION);
    }

    @Override
    public Vector3 interpolate(double t) {
        Vector3 result = new Vector3();

        for(int i = 0; i < points.size(); i++) {
            result = result.plus(points.get(i).scaled(niCoefficient(points.size()-1, i) * Math.pow(1 - t, points.size() - i) * Math.pow(t, i)));
        }

        return result;
    }

    @Override
    public double curveLength() {
        return segmentLength(0, 1);
    }

    @Override
    public double segmentLength(double t0, double t1) {
        Vector3 segmentLength = integral(t0, t1);

        return segmentLength.magnitude();
    }

    public Vector3 integral(double a, double b) {
        double selfIntegralX = CalculusApproximation.integral((double t) -> Math.sqrt(1 + square(selfDerivativeX.evaluate(t))), a, b);
        double selfIntegralY = CalculusApproximation.integral((double t) -> Math.sqrt(1 + square(selfDerivativeY.evaluate(t))), a, b);
        double selfIntegralZ = CalculusApproximation.integral((double t) -> Math.sqrt(1 + square(selfDerivativeZ.evaluate(t))), a, b);

        return new Vector3(selfIntegralX, selfIntegralY, selfIntegralZ);
    }

    public Vector3 derivative(double t) {
        return new Vector3(selfDerivativeX.evaluate(t),
                selfDerivativeY.evaluate(t),
                selfDerivativeZ.evaluate(t));
    }

    private double square(double d) {
        return d*d;
    }

    @Override
    public double findTFromNearest(Vector3 point, double offsetT, double lengthIncrement, double resolution) {
        PathIterator pathIterator = new PathIterator(this, lengthIncrement, resolution);
        pathIterator.setT(offsetT);
        double previousT = offsetT;
        double currentT = offsetT;
        double previousDistance = Double.MAX_VALUE;
        double currentDistance = interpolate(currentT).minus(point).magnitudeSquared();
        Vector3 currentPoint;

        while(previousDistance > currentDistance) {
            previousT = currentT;
            previousDistance = currentDistance;
            if(pathIterator.hasNext()) {
                currentPoint = pathIterator.next();
                currentT = pathIterator.getT();
                currentDistance = currentPoint.minus(point).magnitudeSquared();
            }
            else {
                return previousT;
            }
        }

        return previousT;
    }

    private long niCoefficient(int n, int i) {
        return factorial(n)/(factorial(i)*factorial(n-i));
    }

    public long factorial(int x) {
        long result = 1;

        for(long i = 1; i <= x; i++) {
            result *= i;
        }

        return result;
    }

    public List<Vector3> getPoints() {
        return points;
    }
    void setPoints(Vector3... points) {
        this.points = new ArrayList<>();
        this.points.addAll(Arrays.asList(points));

    }

    public void addPoints(Vector3... newPoints) {
        this.points.addAll(Arrays.asList(newPoints));
    }

    @Override
    public String toString() {
        String result = "";
        int i = 0;

        result += "Degree of curve: " + (points.size()-1) + "\n";
        result += "Control points:\n";
        for(Vector3 point: points) {
            result += "p" + i++ + " = (" + point.x + "; " + point.y + "; " + point.z + ")\n";
        }

        return result;
    }
}
