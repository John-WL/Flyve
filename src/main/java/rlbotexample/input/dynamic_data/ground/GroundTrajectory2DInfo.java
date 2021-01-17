package rlbotexample.input.dynamic_data.ground;

import util.math.vector.Ray2;
import util.math.vector.Vector2;
import util.shapes.Circle;
import util.shapes.CircleArc;

public class GroundTrajectory2DInfo {

    public final CircleArc initialTurn;
    public final Ray2 straightLine;
    public final CircleArc finalTurn;

    public GroundTrajectory2DInfo() {
        this.initialTurn = new CircleArc(new Circle(), 0, 0);
        this.straightLine = new Ray2();
        this.finalTurn = new CircleArc(new Circle(), 0, 0);
    }

    public GroundTrajectory2DInfo(CircleArc initialTurn, Ray2 straightLine, CircleArc finalTurn) {
        this.initialTurn = initialTurn;
        this.straightLine = straightLine;
        this.finalTurn = finalTurn;
    }

    public double length() {
        return initialTurn.length() + straightLine.direction.magnitude() + finalTurn.length();
    }

    public double findAverageSpeed(double t) {
        return length()/t;
    }

    public boolean hasPassedInitialTurn(double t, double speed) {
        double distanceTraveled = speed * (t);

        return distanceTraveled > initialTurn.length();
    }

    public boolean hasPassedStraightLine(double t, double speed) {
        double distanceTraveled = speed * (t);

        return distanceTraveled > (initialTurn.length() + straightLine.direction.magnitude());
    }

    public Vector2 findPointFromElapsedTimeAndSpeed(double t, double speed) {
        double distanceTraveled = speed*t;

        if(distanceTraveled < initialTurn.length()) {
            return initialTurn.findPointFromSpeedAndTimeElapsed(t, speed);
        }
        else if(distanceTraveled < initialTurn.length() + straightLine.direction.magnitude()) {
            double timeToPassInitialTurn = (initialTurn.length()/speed);
            return straightLine.findPointFromSpeedAndTimeElapsed(t-timeToPassInitialTurn, speed);
        }
        else if(distanceTraveled < this.length()) {
            double timeToPassFinalTurn = (initialTurn.length()/speed) + (straightLine.direction.magnitude()/speed);
            return finalTurn.findPointFromSpeedAndTimeElapsed(t-timeToPassFinalTurn, speed);
        }

        // default return so that the universe doesn't explode... we might not use this return that much though
        return new Vector2(Double.NaN, Double.NaN);
    }
}
