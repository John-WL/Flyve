package rlbotexample.input.dynamic_data.aerials;

import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

public class AerialUtils {

    private static Vector3 findConstantAccelerationNeededToReachAerialDestination(ExtendedCarData carData, Vector3 xf, double t) {
        // variable name conversion so that the variable names in my notebook match, please ignore
        Vector3 xi = carData.position;
        Vector3 vi = carData.velocity
                .minus(carData.orientation.roofVector.scaled(carData.hasFirstJump ? RlConstants.ACCELERATION_DUE_TO_JUMP:0))
                .minus(carData.orientation.roofVector.scaled(carData.hasSecondJump ? RlConstants.ACCELERATION_DUE_TO_JUMP:0));

        double ax = findAcceleration(xi.x, xf.x, vi.x, t);
        double ay = findAcceleration(xi.y, xf.y, vi.y, t);
        double az = findAcceleration(xi.z, xf.z, vi.z, t);

        return new Vector3(ax, ay, az);
    }

    private static double findAcceleration(double xi, double xf, double vi, double t) {
        return 2*(xf - xi - (vi*t))/(t*t);
    }

    public static AerialTrajectoryInfo findAerialTrajectoryInfo(ExtendedCarData carData, Trajectory3D trajectory) {
        int precision = 200;
        double amountOfTimeToSearch = 5;
        double boostFactorToHandleSmallRandomErrorInBoostFound = 1.03;
        double desiredAcceleration = RlConstants.ACCELERATION_DUE_TO_BOOST*boostFactorToHandleSmallRandomErrorInBoostFound;

        for(int i = 1; i < precision*amountOfTimeToSearch; i++) {
            double currentTestTime = i/(double)precision;
            Vector3 testAcceleration = findConstantAccelerationNeededToReachAerialDestination(carData, trajectory.compute(currentTestTime), currentTestTime);
            Vector3 testAccelerationWithGravity = testAcceleration.plus(Vector3.UP_VECTOR.scaled(RlConstants.NORMAL_GRAVITY_STRENGTH));
            if(testAccelerationWithGravity.magnitude() < desiredAcceleration) {
                return new AerialTrajectoryInfo(testAccelerationWithGravity, currentTestTime);
            }
        }

        return new AerialTrajectoryInfo();
    }
}
