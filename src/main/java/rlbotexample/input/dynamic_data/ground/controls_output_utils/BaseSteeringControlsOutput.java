package rlbotexample.input.dynamic_data.ground.controls_output_utils;

import rlbotexample.input.dynamic_data.ground.slope_samples.TurningRadiusOfCar2;

public class BaseSteeringControlsOutput {

    public static Double apply(Double desiredSpin, Double velocity) {
        return desiredSpin/findMaxSpin(velocity);
    }

    public static Double findMaxSpin(Double velocity) {
        final double maxRadius = TurningRadiusOfCar2.apply(velocity);
        return velocity/maxRadius;
    }
}
