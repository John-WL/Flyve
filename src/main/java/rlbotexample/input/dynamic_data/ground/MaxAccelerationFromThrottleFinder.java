package rlbotexample.input.dynamic_data.ground;

import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;

public class MaxAccelerationFromThrottleFinder {

    private static LinearApproximator a;
    static {
        a = new LinearApproximator();

        a.sample(new Vector2(0, 1600));
        a.sample(new Vector2(1400, 160));
        a.sample(new Vector2(1410, 0));
        a.sample(new Vector2(2300, 0));
    }

    public static double compute(double v) {
        return a.compute(v);
    }
}
