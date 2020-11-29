package rlbotexample.input.dynamic_data.ground;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

public class GroundDrivingTrajectoryFinder {

    private Trajectory3D trajectory;

    public GroundDrivingTrajectoryFinder(Trajectory3D targetTrajectory) {
        this.trajectory = targetTrajectory;
    }

    public DrivingTrajectoryInfo findDrivingTrajectoryInfo(DataPacket input) {
        return null;
    }
}
