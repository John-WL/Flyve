package rlbotexample.input.dynamic_data.ground.trajectories;

import util.math.vector.Vector3;

public class DrivingTrajectoryInfo {

    public double timeOfDriving;
    public double averageSpeed;
    public Vector3 turningPoint1;
    public Vector3 turningPoint2;

    public DrivingTrajectoryInfo(Vector3 turningPoint1, Vector3 turningPoint2, double averageSpeed, double timeOfDriving) {
        this.turningPoint1 = turningPoint1;
        this.turningPoint2 = turningPoint2;
        this.averageSpeed = averageSpeed;
        this.timeOfDriving = timeOfDriving;
    }
}

