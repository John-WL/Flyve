package rlbotexample.input.dynamic_data.aerials;

import util.math.LinearApproximator;
import util.math.vector.Vector2;

public class PitchVelocityOffsetFinder {

    private static LinearApproximator pitch;
    static {
        pitch = new LinearApproximator();

        pitch.sample(new Vector2(-5.499956, 0.6));
        pitch.sample(new Vector2(-4.392988, 0.5));
        pitch.sample(new Vector2(-2.9005952, 0.4));
        pitch.sample(new Vector2(-1.8441077, 0.3));
        pitch.sample(new Vector2(-1.0568247, 0.2));
        pitch.sample(new Vector2(-0.4947072, 0.1));
        pitch.sample(new Vector2(0, 0));
        pitch.sample(new Vector2(0.4947072, -0.1));
        pitch.sample(new Vector2(1.0568247, -0.2));
        pitch.sample(new Vector2(1.8441077, -0.3));
        pitch.sample(new Vector2(2.9005952, -0.4));
        pitch.sample(new Vector2(4.392988, -0.5));
        pitch.sample(new Vector2(5.499956, -0.6));
    }

    public static double compute(double av) {
        return pitch.compute(av);
    }
}
