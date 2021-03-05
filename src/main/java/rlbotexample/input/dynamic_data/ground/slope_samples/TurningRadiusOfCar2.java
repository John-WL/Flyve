package rlbotexample.input.dynamic_data.ground.slope_samples;

import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;

public class TurningRadiusOfCar2 {

    private static LinearApproximator k;
    static {
        k = new LinearApproximator();

        // this part was performed with no boost, so more precise
        // the speed controller that helped get these result is not that precise, so that's why

        // except this part. It has so little speed it's hard to get accurate results even over a large period of time
        k.sample(new Vector2(0, 0.0073));
        k.sample(new Vector2(5, 0.00727));
        k.sample(new Vector2(15, 0.00701));
        k.sample(new Vector2(45, 0.00645));

        // here the results start to get really precise
        k.sample(new Vector2(77, 0.00626));
        k.sample(new Vector2(97, 0.00614));
        k.sample(new Vector2(117, 0.00602));
        k.sample(new Vector2(147, 0.00585));
        k.sample(new Vector2(197, 0.00558));
        k.sample(new Vector2(298, 0.005048));
        k.sample(new Vector2(395, 0.0045336));
        k.sample(new Vector2(491, 0.0040295));
        k.sample(new Vector2(586, 0.0037028));
        k.sample(new Vector2(680, 0.003397));
        k.sample(new Vector2(773, 0.0030904));
        k.sample(new Vector2(866, 0.0027791));
        k.sample(new Vector2(958, 0.0024698));
        k.sample(new Vector2(1047, 0.0022399));
        k.sample(new Vector2(1132, 0.0020874));
        k.sample(new Vector2(1211, 0.00194165));
        k.sample(new Vector2(1236, 0.0018956));

        // this part was performed with pulsing boost, so a little bit less precise
        k.sample(new Vector2(1300, 0.001708));
        k.sample(new Vector2(1400, 0.00153));
        k.sample(new Vector2(1500, 0.001354));
        k.sample(new Vector2(1600, 0.001255));
        k.sample(new Vector2(1700, 0.00116));
        k.sample(new Vector2(1800, 0.001085));
        k.sample(new Vector2(1900, 0.001040));
        k.sample(new Vector2(2000, 0.000997));
        k.sample(new Vector2(2100, 0.0009504));
        k.sample(new Vector2(2200, 0.0009033));

        // this one was performed under full boost, so it's pretty precise actually
        k.sample(new Vector2(2300, 0.0008602236));
    }

    public static Double apply(Double v) {
        return 1/TurningRadiusOfCar2.k.compute(v);
    }

    public static Double inverse(Double r) {
        return TurningRadiusOfCar2.k.inverse(1/r);
    }
}
