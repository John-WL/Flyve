package rlbotexample.input.dynamic_data.ground.trajectories;

import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.prediction.Trajectory3D;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle3D;

import java.util.function.Function;

public class DrivingTrajectory implements Function<Double, Ray3> {

    public final Ray3 finalPlacement;
    public final ExtendedCarData carData;
    public final Ray3 linearPortion;
    public final double timeSpentOnLinearPortion;
    public final Trajectory3D turningCircle;
    public final double timeSpentOnTurningCircle;
    public final double totalTime;

    public DrivingTrajectory(final Ray3 finalPlacement, final ExtendedCarData carData, final double travelSpeed, ShapeRenderer renderer) {
        this.finalPlacement = finalPlacement;
        this.carData = carData;
        final Circle3D rightCircle = GroundTrajectoryFinder.getRightTurnCircleOnDestination(finalPlacement, carData.orientation.roofVector, travelSpeed);
        final Circle3D leftCircle = GroundTrajectoryFinder.getLeftTurnCircleOnDestination(finalPlacement, carData.orientation.roofVector, travelSpeed);

        Vector3 intersectionBetweenLineAndRightCircle = rightCircle.findTangentPointFrom(carData.position, 0).offset;
        if(Double.isNaN(intersectionBetweenLineAndRightCircle.magnitudeSquared())) {
            intersectionBetweenLineAndRightCircle = rightCircle.findClosestPointFrom(carData.position);
        }
        Vector3 intersectionBetweenLineAndLeftCircle = leftCircle.findTangentPointFrom(carData.position, 1).offset;
        if(Double.isNaN(intersectionBetweenLineAndLeftCircle.magnitudeSquared())) {
            intersectionBetweenLineAndLeftCircle = leftCircle.findClosestPointFrom(carData.position);
        }

        final Trajectory3D rightCircleTrajectory = GroundTrajectoryFinder.getRightTurningTrajectory(
                new Ray3(finalPlacement.offset, finalPlacement.direction.scaledToMagnitude(1)),
                carData.orientation.roofVector,
                travelSpeed);
        final Trajectory3D leftCircleTrajectory = GroundTrajectoryFinder.getLeftTurningTrajectory(
                new Ray3(finalPlacement.offset, finalPlacement.direction.scaledToMagnitude(1)),
                carData.orientation.roofVector,
                travelSpeed);

        final double rightRadiansOfLine = rightCircle.findRadsFromClosestPoint(intersectionBetweenLineAndRightCircle);
        double rightRadiansOfFinalPlacement = rightCircle.findRadsFromClosestPoint(finalPlacement.offset);
        if(rightRadiansOfLine > rightRadiansOfFinalPlacement) {
            rightRadiansOfFinalPlacement += 2*Math.PI;
        }
        final double rightRadians = rightRadiansOfFinalPlacement - rightRadiansOfLine;
        final double timeToTravelOnRightCircle = rightRadians * rightCircle.radii / travelSpeed;
        final double timeToTravelOnRightLinearPart = intersectionBetweenLineAndRightCircle
                .minus(carData.position)
                .magnitude() / travelSpeed;

        final double leftRadiansOfLine = leftCircle.findRadsFromClosestPoint(intersectionBetweenLineAndLeftCircle);
        double leftRadiansOfFinalPlacement = leftCircle.findRadsFromClosestPoint(finalPlacement.offset);
        if(leftRadiansOfLine > leftRadiansOfFinalPlacement) {
            leftRadiansOfFinalPlacement += 2*Math.PI;
        }
        final double leftRadians = leftRadiansOfFinalPlacement - leftRadiansOfLine;
        final double timeToTravelOnLeftCircle = leftRadians * leftCircle.radii / travelSpeed;
        final double timeToTravelOnLeftLinearPart = intersectionBetweenLineAndLeftCircle
                .minus(carData.position)
                .magnitude() / travelSpeed;

        if((Math.PI - timeToTravelOnRightCircle) + timeToTravelOnRightLinearPart
                < timeToTravelOnLeftCircle + timeToTravelOnLeftLinearPart) {
            this.linearPortion = new Ray3(
                    carData.position,
                    intersectionBetweenLineAndRightCircle.minus(carData.position));
            this.turningCircle = rightCircleTrajectory;
            this.timeSpentOnLinearPortion = timeToTravelOnRightLinearPart;
            this.timeSpentOnTurningCircle = timeToTravelOnRightCircle;
        }
        else {
            this.linearPortion = new Ray3(
                    carData.position,
                    intersectionBetweenLineAndLeftCircle.minus(carData.position));
            this.turningCircle = leftCircleTrajectory;
            this.timeSpentOnLinearPortion = timeToTravelOnLeftLinearPart;
            this.timeSpentOnTurningCircle = -timeToTravelOnLeftCircle;
        }
        this.totalTime = timeSpentOnLinearPortion + timeSpentOnTurningCircle;
    }

    @Override
    public Ray3 apply(final Double time) {
        if(time < timeSpentOnLinearPortion) {
            final double parametricT = time/timeSpentOnLinearPortion;
            final Vector3 offset = linearPortion.offset
                    .scaled(1-parametricT)
                    .plus(linearPortion.offset.plus(linearPortion.direction)
                            .scaled(parametricT));
            final Vector3 direction = linearPortion.offset.normalized();
            return new Ray3(offset, direction);
        }

        double transposedTime = timeSpentOnTurningCircle + (time-timeSpentOnLinearPortion);
        return new Ray3(
                turningCircle.apply(transposedTime),
                turningCircle.derivative(transposedTime));
    }
}
