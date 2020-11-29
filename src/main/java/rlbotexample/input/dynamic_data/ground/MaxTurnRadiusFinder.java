package rlbotexample.input.dynamic_data.ground;

import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.prediction.Trajectory3D;
import util.math.LinearApproximator;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class MaxTurnRadiusFinder {

    private static LinearApproximator k;
    static {
        k = new LinearApproximator();

        k.sample(new Vector2(0, 1/0.0069));
        k.sample(new Vector2(500, 1/0.00398));
        k.sample(new Vector2(1000, 1/0.00235));
        k.sample(new Vector2(1500, 1/0.001375));
        k.sample(new Vector2(1750, 1/0.0011));
        k.sample(new Vector2(2300, 1/0.00088));
    }

    public static double compute(double v) {
        return k.compute(v);
    }
}
