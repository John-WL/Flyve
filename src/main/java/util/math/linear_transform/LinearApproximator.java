package util.math.linear_transform;

import util.math.vector.Vector2;
import util.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LinearApproximator {

    public List<Vector2> functionSamples;

    public LinearApproximator() {
        functionSamples = new ArrayList<>();
    }

    public LinearApproximator(Function<Double, Double> function, double min, double max, int amountOfSamples) {
        functionSamples = new ArrayList<>();
        double rangeSize = java.lang.Math.abs(max-min);
        for(int i = 0; i < amountOfSamples; i++) {
            double x = min + (rangeSize*i/amountOfSamples);
            sample(new Vector2(x, function.apply(x)));
        }
    }

    public void sample(Vector2 sampledPoint) {
        for(int i = 0; i < functionSamples.size(); i++) {
            if(functionSamples.get(i).x > sampledPoint.x) {
                functionSamples.add(i, sampledPoint);
                return;
            }
        }
        functionSamples.add(sampledPoint);
    }

    // functional approximation
    public double compute(double x) {
        Vector2 closestPoint = new Vector2();
        double closestDistance = Double.MAX_VALUE;

        for(Vector2 element: functionSamples) {
            if(java.lang.Math.abs(element.x - x) < closestDistance) {
                closestPoint = element;
                closestDistance = java.lang.Math.abs(element.x - x);
            }
        }

        Vector2 secondClosestPoint;
        int indexOfClosestPoint = functionSamples.indexOf(closestPoint);
        // if the point found is to the right of x
        if(closestPoint.x - x > 0) {
            if(indexOfClosestPoint == 0) {
                secondClosestPoint = functionSamples.get(indexOfClosestPoint+1);
            }
            else {
                secondClosestPoint = functionSamples.get(indexOfClosestPoint-1);
            }
        }
        // if the point found is to the left of x
        else {
            if(indexOfClosestPoint == functionSamples.size()-1) {
                secondClosestPoint = functionSamples.get(indexOfClosestPoint-1);
            }
            else {
                secondClosestPoint = functionSamples.get(indexOfClosestPoint+1);
            }
        }

        double t = (x - secondClosestPoint.x) / (closestPoint.x - secondClosestPoint.x);
        return closestPoint.y*t + secondClosestPoint.y*(1-t);
        /*double totalClosestDistance = closestDistance + secondClosestDistance;

        if(totalClosestDistance == 0) {
            return closestPoint.y;
        }

        return (closestPoint.y * (1-(closestDistance/totalClosestDistance)))
                + (secondClosestPoint.y * (1-(secondClosestDistance/totalClosestDistance)));*/
    }

    // inverse approximation
    public double inverse(double y) {
        Vector2 closestPoint = new Vector2();
        double closestDistance = Double.MAX_VALUE;

        for(Vector2 element: functionSamples) {
            if(java.lang.Math.abs(element.y - y) < closestDistance) {
                closestPoint = element;
                closestDistance = java.lang.Math.abs(element.y - y);
            }
        }

        List<Vector2> functionSamplesCopy = new ArrayList<>(functionSamples);
        functionSamplesCopy.remove(closestPoint);

        Vector2 secondClosestPoint = new Vector2();
        double secondClosestDistance = Double.MAX_VALUE;

        for(Vector2 element: functionSamplesCopy) {
            if(java.lang.Math.abs(element.y - y) < secondClosestDistance) {
                secondClosestPoint = element;
                secondClosestDistance = java.lang.Math.abs(element.y - y);
            }
        }

        double totalClosestDistance = closestDistance + secondClosestDistance;

        if(totalClosestDistance == 0) {
            return closestPoint.x;
        }

        return (closestPoint.x * (closestDistance/totalClosestDistance))
                + (secondClosestPoint.x * (secondClosestDistance/totalClosestDistance));
    }

}
