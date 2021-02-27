package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.orientation6_functions;

import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;

public class YawVelocityOffsetFinder {

    private static LinearApproximator yaw;
    static {
        yaw = new LinearApproximator();

        yaw.sample(new Vector2(-5.499907, 0.6));
        yaw.sample(new Vector2(-4.8130074, 0.5));
        yaw.sample(new Vector2(-3.176143, 0.4));
        yaw.sample(new Vector2(-2.019513, 0.3));
        yaw.sample(new Vector2(-1.1565205, 0.2));
        yaw.sample(new Vector2(-0.5422797, 0.1));
        yaw.sample(new Vector2(0, 0));
        yaw.sample(new Vector2(0.5422797, -0.1));
        yaw.sample(new Vector2(1.1565205, -0.2));
        yaw.sample(new Vector2(2.019513, -0.3));
        yaw.sample(new Vector2(3.176143, -0.4));
        yaw.sample(new Vector2(4.8130074, -0.5));
        yaw.sample(new Vector2(5.499907, -0.6));
    }

    public static double compute(double av) {
        return yaw.compute(av);
    }
}
