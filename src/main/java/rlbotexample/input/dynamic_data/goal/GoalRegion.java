package rlbotexample.input.dynamic_data.goal;

import util.math.vector.Vector3;

public class GoalRegion {

    public final Vector3 upperLeft;
    public final Vector3 lowerRight;

    public GoalRegion(Vector3 upperLeft, Vector3 lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }


}
