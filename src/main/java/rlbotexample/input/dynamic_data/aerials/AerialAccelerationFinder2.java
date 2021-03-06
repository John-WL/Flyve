package rlbotexample.input.dynamic_data.aerials;

import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Vector3;

import java.util.Optional;
import java.util.function.Function;

public class AerialAccelerationFinder2 {

    private static final double BOOST_FACTOR_TO_HANDLE_SMALL_RANDOM_ERROR_IN_CALCULATION = 1.032;

    private static Vector3 findConstantAccelerationNeededToReachAerialDestination(CarData carData, Vector3 xf, double t) {
        // if we are considering a time when the ball is considered invalid
        if(xf == null) {
            return new Vector3();
        }
        Vector3 xi = carData.position;
        Vector3 vi = carData.velocity;

        return new Vector3(
                findAcceleration(xi.x, xf.x, vi.x, t),
                findAcceleration(xi.y, xf.y, vi.y, t),
                findAcceleration(xi.z, xf.z, vi.z, t));
    }

    private static double findAcceleration(double xi, double xf, double vi, double t) {
        return 2*(xf - xi - (vi*t))/(t*t);
    }

    public static Optional<AerialTrajectoryInfo> findAerialTrajectoryInfoOrElse(Function<Double, Vector3> targetTrajectory,
                                                                                CarData carData,
                                                                                double maxDeltaV) {
        double precision = 1.0/120;
        double amountOfTimeToSearch = 5;
        double maxAcceleration = RlConstants.ACCELERATION_DUE_TO_BOOST_IN_AIR
                * BOOST_FACTOR_TO_HANDLE_SMALL_RANDOM_ERROR_IN_CALCULATION;

        for(int i = 1; i*precision < amountOfTimeToSearch; i++) {
            double currentTestTime = i*precision;
            Vector3 targetPosition = targetTrajectory.apply(currentTestTime);
            if(targetPosition == null) continue;
            Vector3 testAcceleration = findConstantAccelerationNeededToReachAerialDestination(
                    carData,
                    targetPosition,
                    currentTestTime)
                    .minus(RlConstants.GRAVITY_VECTOR);

            if(testAcceleration.magnitude() > maxAcceleration) continue;
            if(testAcceleration.scaled(currentTestTime).magnitude() > maxDeltaV) continue;

            return Optional.of(new AerialTrajectoryInfo(testAcceleration, currentTestTime));
        }

        return Optional.empty();
    }
}
