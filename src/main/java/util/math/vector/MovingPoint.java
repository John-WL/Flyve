package util.math.vector;

public class MovingPoint {
    public Ray3 currentState;
    public double time;

    public MovingPoint() {
        this(new Ray3(), 0);
    }

    public MovingPoint(Ray3 currentState, double time) {
        this.currentState = currentState;
        this.time = time;
    }
}
