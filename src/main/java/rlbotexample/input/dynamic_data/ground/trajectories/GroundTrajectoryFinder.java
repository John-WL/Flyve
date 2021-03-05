package rlbotexample.input.dynamic_data.ground.trajectories;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.ground.slope_samples.TurningRadiusOfCar2;
import rlbotexample.input.prediction.Trajectory3D;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.shapes.Circle3D;

public class GroundTrajectoryFinder {

    private Trajectory3D targetTrajectory;
    private Trajectory3D orientationTrajectory;

    public GroundTrajectoryFinder(Trajectory3D targetTrajectory, Trajectory3D orientationTrajectory) {
        this.targetTrajectory = targetTrajectory;
        this.orientationTrajectory = orientationTrajectory;
    }

    public static Circle3D getRightTurnCircleOnDestination(Ray3 ray, Vector3 carRoof, double velocity) {
        double radii = TurningRadiusOfCar2.apply(velocity);
        Vector3 offset = ray.direction.crossProduct(carRoof).scaled(radii);
        //return new Circle3D(new Ray3(ray.offset.minus(ray.direction.scaled(-velocity/28.75)).plus(offset), carRoof), radii);
        return new Circle3D(new Ray3(ray.offset.plus(offset), carRoof), radii);
    }

    public static Circle3D getLeftTurnCircleOnDestination(Ray3 ray, Vector3 carRoof, double velocity) {
        double radii = TurningRadiusOfCar2.apply(velocity);
        Vector3 offset = ray.direction.crossProduct(carRoof).scaled(radii);
        //return new Circle3D(new Ray3(ray.offset.minus(ray.direction.scaled(-velocity/28.75)).minus(offset), carRoof), radii);
        return new Circle3D(new Ray3(ray.offset.minus(offset), carRoof), radii);
    }

    public static Trajectory3D getRightTurningTrajectory(ExtendedCarData carData) {
        double radii = TurningRadiusOfCar2.apply(carData.velocity.magnitude());
        Circle3D rightTurn = getRightTurnCircleOnDestination(new Ray3(carData.position, carData.orientation.noseVector), carData.orientation.roofVector, carData.velocity.magnitude());

        double angularMomentum = carData.velocity.dotProduct(carData.orientation.noseVector)/radii;

        return time -> {
            double rads = rightTurn.findRadsFromClosestPoint(carData.position);
            return rightTurn.findPointOnCircle(rads - (angularMomentum*time));
        };
    }

    public static Trajectory3D getLeftTurningTrajectory(ExtendedCarData carData) {
        double radii = TurningRadiusOfCar2.apply(carData.velocity.magnitude());
        Circle3D rightTurn = getLeftTurnCircleOnDestination(new Ray3(carData.position, carData.orientation.noseVector), carData.orientation.roofVector, carData.velocity.magnitude());

        double angularMomentum = carData.velocity.dotProduct(carData.orientation.noseVector)/radii;

        return time -> {
            double rads = rightTurn.findRadsFromClosestPoint(carData.position);
            return rightTurn.findPointOnCircle(rads + (angularMomentum*time));
        };
    }

    public static Trajectory3D getRightTurningTrajectory(Ray3 ray, Vector3 roofOrientation, double velocity) {
        double radii = TurningRadiusOfCar2.apply(velocity);
        Circle3D rightTurn = getRightTurnCircleOnDestination(ray, roofOrientation, velocity);

        double angularMomentum = velocity/radii;

        return time -> {
            double rads = rightTurn.findRadsFromClosestPoint(ray.offset);
            return rightTurn.findPointOnCircle(rads - (angularMomentum*time));
        };
    }

    public static Trajectory3D getLeftTurningTrajectory(Ray3 ray, Vector3 roofOrientation, double velocity) {
        double radii = TurningRadiusOfCar2.apply(velocity);
        Circle3D rightTurn = getLeftTurnCircleOnDestination(ray, roofOrientation, velocity);

        double angularMomentum = velocity/radii;

        return time -> {
            double rads = rightTurn.findRadsFromClosestPoint(ray.offset);
            return rightTurn.findPointOnCircle(rads + (angularMomentum*time));
        };
    }

    private DrivingTrajectoryInfo findConstantSpeedNeededToReachGroundDestination(ExtendedCarData carData, Vector3 xf, Vector3 of, double t) {
        Vector3 xi = carData.position;

        Circle3D carCircle = getLeftTurnCircleOnDestination(new Ray3(carData.position, carData.orientation.noseVector), carData.orientation.roofVector, carData.velocity.magnitude());
        Circle3D ballCircle = getLeftTurnCircleOnDestination(new Ray3(xf, of.minus(xf).normalized()), carData.orientation.roofVector, carData.velocity.magnitude());

        Vector3 distanceBetweenCircles = ballCircle.center.offset.minus(carCircle.center.offset);

        Vector3 vectorToFindPoints = distanceBetweenCircles.orderedPlusAngle(new Vector3(0, -1, 0));
        Vector3 point1 = carCircle.findClosestPointFrom(vectorToFindPoints.plus(carCircle.center.offset));
        Vector3 point2 = ballCircle.findClosestPointFrom(vectorToFindPoints.plus(ballCircle.center.offset));

        Ray3 straightLineToTravel = new Ray3(point1, point2.minus(point1));

        double angleToTravelOnCarCircle = xi.minus(carCircle.center.offset).angle(point1.minus(carCircle.center.offset));
        double arcDistanceToTravelOnCarCircle = angleToTravelOnCarCircle*carCircle.radii;
        double angleToTravelOnBallCircle = point2.minus(ballCircle.center.offset).angle(xf.minus(ballCircle.center.offset));
        double arcDistanceToTravelOnBallCircle = angleToTravelOnBallCircle*ballCircle.radii;

        double totalDistanceToTravel = straightLineToTravel.direction.magnitude() + arcDistanceToTravelOnCarCircle + arcDistanceToTravelOnBallCircle;
        double averageSpeed = totalDistanceToTravel/t;

        return new DrivingTrajectoryInfo(point1, point2, averageSpeed, t);
    }

    public DrivingTrajectoryInfo findDrivingTrajectoryInfo(DataPacket input) {
        int precision = 120;
        double amountOfTimeToSearch = 10;
        double desiredAverageSpeed = input.car.velocity.magnitude();

        for (int i = 1; i < precision * amountOfTimeToSearch; i++) {
            double currentTestTime = i/(double)precision;
            DrivingTrajectoryInfo info = findConstantSpeedNeededToReachGroundDestination(input.car, targetTrajectory.apply(currentTestTime), orientationTrajectory.apply(currentTestTime), currentTestTime);
            if(info.averageSpeed < desiredAverageSpeed) {
                return info;
            }
        }

        return new DrivingTrajectoryInfo(new Vector3(), new Vector3(), 0, 0);
    }
}
