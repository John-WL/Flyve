package rlbotexample.input.dynamic_data.aerials;

import util.math.vector.Vector3;

public class AerialTrajectoryInfo {

    public Vector3 acceleration;
    public double timeOfFlight;

    public AerialTrajectoryInfo() {
        this.acceleration = new Vector3();
        this.timeOfFlight = 0;
    }

    public AerialTrajectoryInfo(Vector3 acceleration, double timeOfFlight) {
        this.acceleration = acceleration;
        this.timeOfFlight = timeOfFlight;
    }
}
