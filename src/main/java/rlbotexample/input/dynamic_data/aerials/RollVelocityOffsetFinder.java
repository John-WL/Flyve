package rlbotexample.input.dynamic_data.aerials;

import util.math.LinearApproximator;
import util.math.vector.Vector2;

public class RollVelocityOffsetFinder {

    private static LinearApproximator roll;
    static {
        roll = new LinearApproximator();

        roll.sample(new Vector2(-5.499911, -0.7));
        roll.sample(new Vector2(-4.7850237, -0.6));
        roll.sample(new Vector2(-4.029174, -0.5));
        roll.sample(new Vector2(-3.210172, -0.4));
        roll.sample(new Vector2(-2.391322, -0.3));
        roll.sample(new Vector2(-1.5723773, -0.2));
        roll.sample(new Vector2(-0.8165458, -0.1));
        roll.sample(new Vector2(0, 0));
        roll.sample(new Vector2(0.8165458, 0.1));
        roll.sample(new Vector2(1.5723773, 0.2));
        roll.sample(new Vector2(2.391322, 0.3));
        roll.sample(new Vector2(3.210172, 0.4));
        roll.sample(new Vector2(4.029174, 0.5));
        roll.sample(new Vector2(4.7850237, 0.6));
        roll.sample(new Vector2(5.499911, 0.7));
    }

    public static double compute(double av) {
        return roll.compute(av);
    }
}
