package util.bezier_curve;

import util.vector.Vector3;

import java.util.*;

public class BezierCurve implements CurveSegment {

    private List<Vector3> points;

    public BezierCurve(Vector3... points)
    {
        this.points = new ArrayList<>();
        this.points.addAll(Arrays.asList(points));
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
    public double curveLength(int resolution) {
        return segmentLength(0, 1, resolution);
    }

    @Override
    public double segmentLength(double t0, double t1, int resolution) {
        Vector3 currentInterpolation = interpolate(t0);
        Vector3 lastInterpolation;
        double totalLength = 0;

        for(int i = 1; i < resolution; i++)
        {
            lastInterpolation = currentInterpolation;
            currentInterpolation = interpolate(t0 + (t1-t0)*(i/(double)resolution));
            totalLength += currentInterpolation.minus(lastInterpolation).magnitude();
        }

        return totalLength;
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

    private int niCoefficient(int n, int i) {
        return factorial(n)/(factorial(i)*factorial(n-i));
    }

    private int factorial(int x) {
        int result = 1;

        for(int i = 1; i < x; i++) {
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
}
