package rlbotexample.input.dynamic_data.aerials;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Vector3;

public class AerialAccelerationFinder {

    private static final double BOOST_FACTOR_TO_HANDLE_SMALL_RANDOM_ERROR_IN_CALCULATION = 1.032;

    private Trajectory3D targetTrajectory;

    public AerialAccelerationFinder(Trajectory3D targetTrajectory) {
        this.targetTrajectory = targetTrajectory;
    }

    private Vector3 findConstantAccelerationNeededToReachAerialDestination(ExtendedCarData carData, Vector3 xf, double t) {
        Vector3 xi = carData.position;
        // a bit of ugliness... Basically, handle jump initial velocity if we can jump.
        // the problem with this is that even if we have the second jump, we don't necessarily want to use it straight away.
        // same of the first jump if we are on the ceiling. We don't necessarily want to use it, as it can add an unnecessary/annoying velocity to deal with.
        Vector3 vi = carData.velocity
                .minus(carData.orientation.roofVector.scaled(carData.hasFirstJump ? RlConstants.ACCELERATION_DUE_TO_JUMP : 0))
                //.minus(carData.orientation.roofVector.scaled(carData.hasSecondJump ? RlConstants.ACCELERATION_DUE_TO_JUMP : 0))
        ;

        return findAcceleration(xi, xf, vi, t);
    }

    private double findAcceleration(double xi, double xf, double vi, double t) {
        return 2*(xf - xi - (vi*t))/(t*t);
    }

    private Vector3 findAcceleration(Vector3 xi, Vector3 xf, Vector3 vi, double t) {
        return (xf.minus(xi).minus(vi.scaled(t)))
                .scaled(2/(t*t));
    }

    private AerialTrajectoryInfo findAerialTrajectoryInfo(DataPacket input) {
        int precision = 120;
        double amountOfTimeToSearch = 10;
        double desiredAcceleration = RlConstants.ACCELERATION_DUE_TO_BOOST*BOOST_FACTOR_TO_HANDLE_SMALL_RANDOM_ERROR_IN_CALCULATION;

        for(int i = 1; i < precision*amountOfTimeToSearch; i++) {
            double currentTestTime = i/(double)precision;
            Vector3 testAcceleration = findConstantAccelerationNeededToReachAerialDestination(input.car, targetTrajectory.compute(currentTestTime), currentTestTime);
            Vector3 testAccelerationWithGravity = testAcceleration.plus(Vector3.UP_VECTOR.scaled(RlConstants.NORMAL_GRAVITY_STRENGTH));
            if(testAccelerationWithGravity.magnitude() < desiredAcceleration) {
                return new AerialTrajectoryInfo(testAccelerationWithGravity, currentTestTime);
            }
        }

        return new AerialTrajectoryInfo();
    }

    public AerialTrajectoryInfo findAerialTrajectoryInfo(double timeToTryFirst, DataPacket input) {
        double maxAcceleration = RlConstants.ACCELERATION_DUE_TO_BOOST*BOOST_FACTOR_TO_HANDLE_SMALL_RANDOM_ERROR_IN_CALCULATION;

        Vector3 testAcceleration = findConstantAccelerationNeededToReachAerialDestination(input.car, targetTrajectory.compute(timeToTryFirst), timeToTryFirst)
                .plus(Vector3.UP_VECTOR.scaled(RlConstants.NORMAL_GRAVITY_STRENGTH));

        if(testAcceleration.magnitude() < maxAcceleration) {
            return new AerialTrajectoryInfo(testAcceleration, timeToTryFirst);
        }

        return findAerialTrajectoryInfo(input);
    }

    private AerialTrajectoryInfo findAerialTrajectoryInfo2(DataPacket input) {
        Trajectory3D accelerationCurve = time ->
                findConstantAccelerationNeededToReachAerialDestination(input.car, targetTrajectory.compute(time), time)
                        .plus(Vector3.UP_VECTOR.scaled(RlConstants.NORMAL_GRAVITY_STRENGTH));

        accelerationCurve
                .keep(movingPoint ->
                        movingPoint.currentState.offset.magnitudeSquared()
                                < RlConstants.ACCELERATION_DUE_TO_BOOST * RlConstants.ACCELERATION_DUE_TO_BOOST);
        MovingPoint accelerationInfo = accelerationCurve.first(5, 120);

        if(accelerationInfo != null) {
            return new AerialTrajectoryInfo(accelerationInfo.currentState.offset, accelerationInfo.time);
        }
        return new AerialTrajectoryInfo();
    }
}
