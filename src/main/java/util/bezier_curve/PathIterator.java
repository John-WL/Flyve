package util.bezier_curve;

import util.vector.Vector3;


public class PathIterator {

    private double lengthIncrement;
    private CurveSegment path;
    private double currentT;
    private double precision;

    public PathIterator(CurveSegment path, double lengthIncrement, double precision) {
        this.path = path;
        this.lengthIncrement = lengthIncrement;
        this.precision = precision;
        this.currentT = 0;
    }

    public Vector3 next() {
        if(!hasNext()) throw new IndexOutOfBoundsException("Cannot get the next element!");

        double divisor = (1 - currentT)/2;
        double newT = currentT + divisor;
        double currentSegmentLength = 0;

        while(Math.abs(currentSegmentLength - lengthIncrement) > precision/2) {
            divisor /= 2;
            currentSegmentLength = path.segmentLength(currentT, newT);

            if(currentSegmentLength < lengthIncrement) {
                newT += divisor;
            }
            else {
                newT -= divisor;
            }
        }

        currentT = newT;

        return path.interpolate(currentT);
    }

    public void pathLengthIncreased(int numberOfAddedPaths, int numberOfPaths) {
        // updating the t variable in the path composite
        currentT = (currentT * numberOfPaths) / (numberOfPaths + numberOfAddedPaths);
    }

    public boolean hasNext() {
        return path.segmentLength(currentT, 1) > lengthIncrement;
    }

    public void setLengthIncrement(double lengthIncrement) {
        this.lengthIncrement = lengthIncrement;
    }

    public double getT() { return currentT; }
    public void setT(double newT) { currentT = newT; }
}
