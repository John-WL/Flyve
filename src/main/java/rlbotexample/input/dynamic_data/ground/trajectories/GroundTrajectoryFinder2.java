package rlbotexample.input.dynamic_data.ground.trajectories;

import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.ground.slope_samples.TurningRadiusOfCar;
import rlbotexample.input.prediction.Trajectory3D;
import util.math.vector.Ray2;
import util.math.vector.Vector2;
import util.shapes.Circle;
import util.shapes.CircleArc;

public class GroundTrajectoryFinder2 {

    private Trajectory3D targetTrajectory;
    private Trajectory3D orientationTrajectory;

    public GroundTrajectoryFinder2(Trajectory3D targetTrajectory, Trajectory3D orientationTrajectory) {
        this.targetTrajectory = targetTrajectory;
        this.orientationTrajectory = orientationTrajectory;
    }

    public Circle getRightTurningCircle(Ray2 position, double velocity) {
        double radii = TurningRadiusOfCar.apply(velocity);
        Vector2 offset = position.direction.minusAngle(new Vector2(0, 1)).scaledToMagnitude(radii);
        return new Circle(position.offset.minus(position.direction.scaledToMagnitude(-velocity/28.75)).plus(offset), radii);
    }

    public Circle getLeftTurningCircle(Ray2 position, double velocity) {
        double radii = TurningRadiusOfCar.apply(velocity);
        Vector2 offset = position.direction.plusAngle(new Vector2(0, 1)).scaledToMagnitude(radii);
        return new Circle(position.offset.minus(position.direction.scaledToMagnitude(-velocity/28.75)).plus(offset), radii);
    }

    // OMG AHHHH WHAT AM I DOINGGG AHHHH
    public GroundTrajectory2DInfo[] findAllGroundTrajectories2DInfo(Ray2 carPosition, Ray2 carDestination, double carVelocity) {
        Circle carRightTurn = getRightTurningCircle(carPosition, carVelocity);
        Circle carLeftTurn = getLeftTurningCircle(carPosition, carVelocity);
        Circle carDestinationRightTurn = getRightTurningCircle(carDestination, carVelocity);
        Circle carDestinationLeftTurn = getLeftTurningCircle(carDestination, carVelocity);

        Ray2 rightRightTangent = carRightTurn.findTangentsFrom(carDestinationRightTurn)[1];
        Ray2 rightLeftTangent = carRightTurn.findTangentsFrom(carDestinationLeftTurn)[2];
        Ray2 leftRightTangent = carLeftTurn.findTangentsFrom(carDestinationRightTurn)[3];
        Ray2 leftLeftTangent = carLeftTurn.findTangentsFrom(carDestinationLeftTurn)[0];

        double startingRadsForRightRightTurnTrajectory0 = carRightTurn.findRadsFromPoint(carPosition.offset);
        double endingRadsForRightRightTurnTrajectory0 = carRightTurn.findRadsFromPoint(rightRightTangent.offset);
        if(endingRadsForRightRightTurnTrajectory0 > startingRadsForRightRightTurnTrajectory0) {
            endingRadsForRightRightTurnTrajectory0 -= 2*Math.PI;
        }
        double startingRadsForRightRightTurnTrajectory1 = carDestinationRightTurn.findRadsFromPoint(rightRightTangent.offset.plus(rightRightTangent.direction));
        double endingRadsForRightRightTurnTrajectory1 = carDestinationRightTurn.findRadsFromPoint(carDestination.offset);
        if(endingRadsForRightRightTurnTrajectory1 > startingRadsForRightRightTurnTrajectory1) {
            endingRadsForRightRightTurnTrajectory1 -= 2*Math.PI;
        }
        CircleArc rightRightTurnTrajectory0 = new CircleArc(carRightTurn,
                startingRadsForRightRightTurnTrajectory0,
                endingRadsForRightRightTurnTrajectory0);
        CircleArc rightRightTurnTrajectory1 = new CircleArc(carDestinationRightTurn,
                startingRadsForRightRightTurnTrajectory1,
                endingRadsForRightRightTurnTrajectory1);

        double startingRadsForRightLeftTurnTrajectory0 = carRightTurn.findRadsFromPoint(carPosition.offset);
        double endingRadsForRightLeftTurnTrajectory0 = carRightTurn.findRadsFromPoint(rightLeftTangent.offset);
        if(endingRadsForRightLeftTurnTrajectory0 > startingRadsForRightLeftTurnTrajectory0) {
            endingRadsForRightLeftTurnTrajectory0 -= 2*Math.PI;
        }
        double startingRadsForRightLeftTurnTrajectory1 = carDestinationLeftTurn.findRadsFromPoint(rightLeftTangent.offset.plus(rightLeftTangent.direction));
        double endingRadsForRightLeftTurnTrajectory1 = carDestinationLeftTurn.findRadsFromPoint(carDestination.offset);
        if(endingRadsForRightLeftTurnTrajectory1 < startingRadsForRightLeftTurnTrajectory1) {
            endingRadsForRightLeftTurnTrajectory1 += 2*Math.PI;
        }
        CircleArc rightLeftTurnTrajectory0 = new CircleArc(carRightTurn,
                startingRadsForRightLeftTurnTrajectory0,
                endingRadsForRightLeftTurnTrajectory0);
        CircleArc rightLeftTurnTrajectory1 = new CircleArc(carDestinationLeftTurn,
                startingRadsForRightLeftTurnTrajectory1,
                endingRadsForRightLeftTurnTrajectory1);

        double startingRadsForLeftRightTurnTrajectory0 = carLeftTurn.findRadsFromPoint(carPosition.offset);
        double endingRadsForLeftRightTurnTrajectory0 = carLeftTurn.findRadsFromPoint(leftRightTangent.offset);
        if(endingRadsForLeftRightTurnTrajectory0 < startingRadsForLeftRightTurnTrajectory0) {
            endingRadsForLeftRightTurnTrajectory0 += 2*Math.PI;
        }
        double startingRadsForLeftRightTurnTrajectory1 = carDestinationRightTurn.findRadsFromPoint(leftRightTangent.offset.plus(leftRightTangent.direction));
        double endingRadsForLeftRightTurnTrajectory1 = carDestinationRightTurn.findRadsFromPoint(carDestination.offset);
        if(endingRadsForLeftRightTurnTrajectory1 > startingRadsForLeftRightTurnTrajectory1) {
            endingRadsForLeftRightTurnTrajectory1 -= 2*Math.PI;
        }
        CircleArc leftRightTurnTrajectory0 = new CircleArc(carLeftTurn,
                startingRadsForLeftRightTurnTrajectory0,
                endingRadsForLeftRightTurnTrajectory0);
        CircleArc leftRightTurnTrajectory1 = new CircleArc(carDestinationRightTurn,
                startingRadsForLeftRightTurnTrajectory1,
                endingRadsForLeftRightTurnTrajectory1);

        double startingRadsForLeftLeftTurnTrajectory0 = carLeftTurn.findRadsFromPoint(carPosition.offset);
        double endingRadsForLeftLeftTurnTrajectory0 = carLeftTurn.findRadsFromPoint(leftLeftTangent.offset);
        if(endingRadsForLeftLeftTurnTrajectory0 < startingRadsForLeftLeftTurnTrajectory0) {
            endingRadsForLeftLeftTurnTrajectory0 += 2*Math.PI;
        }
        double startingRadsForLeftLeftTurnTrajectory1 = carDestinationLeftTurn.findRadsFromPoint(leftLeftTangent.offset.plus(leftLeftTangent.direction));
        double endingRadsForLeftLeftTurnTrajectory1 = carDestinationLeftTurn.findRadsFromPoint(carDestination.offset);
        if(endingRadsForLeftLeftTurnTrajectory1 < startingRadsForLeftLeftTurnTrajectory1) {
            endingRadsForLeftLeftTurnTrajectory1 += 2*Math.PI;
        }
        CircleArc leftLeftTurnTrajectory0 = new CircleArc(carLeftTurn,
                startingRadsForLeftLeftTurnTrajectory0,
                endingRadsForLeftLeftTurnTrajectory0);
        CircleArc leftLeftTurnTrajectory1 = new CircleArc(carDestinationLeftTurn,
                startingRadsForLeftLeftTurnTrajectory1,
                endingRadsForLeftLeftTurnTrajectory1);

        return new GroundTrajectory2DInfo[] {
                new GroundTrajectory2DInfo(rightRightTurnTrajectory0, rightRightTangent, rightRightTurnTrajectory1),
                new GroundTrajectory2DInfo(rightLeftTurnTrajectory0, rightLeftTangent, rightLeftTurnTrajectory1),
                new GroundTrajectory2DInfo(leftRightTurnTrajectory0, leftRightTangent, leftRightTurnTrajectory1),
                new GroundTrajectory2DInfo(leftLeftTurnTrajectory0, leftLeftTangent, leftLeftTurnTrajectory1)
        };
    }

    public GroundTrajectory2DInfo findFastestGroundTrajectory2DInfo(Ray2 carPosition, Ray2 carDestination, double carVelocity) {
        GroundTrajectory2DInfo[] groundTrajectories2D = findAllGroundTrajectories2DInfo(carPosition, carDestination, carVelocity);
        GroundTrajectory2DInfo fastestTrack = new GroundTrajectory2DInfo();
        double shortestLength = Double.MAX_VALUE;

        for(GroundTrajectory2DInfo element: groundTrajectories2D) {
            double trackLength = element.length();
            if(trackLength < shortestLength) {
                shortestLength = trackLength;
                fastestTrack = element;
            }
        }

        return fastestTrack;
    }

    public GroundTrajectory2DInfo findGroundTrajectory2DInfo(ExtendedCarData car) {
        int precision = 60;
        double amountOfTimeToSearch = 20;
        double desiredSpeed = Math.max(car.velocity.magnitude(), 500);

        for(int i = 1; i < precision*amountOfTimeToSearch; i++) {
            double currentTestTime = i/(double)precision;
            if(targetTrajectory.apply(currentTestTime) == null) {
                continue;
            }
            Ray2 position = new Ray2(
                    car.position.flatten(),
                    car.orientation.noseVector.flatten());
            Ray2 destination = new Ray2(
                    targetTrajectory.apply(currentTestTime).flatten(),
                    orientationTrajectory.apply(currentTestTime).flatten());
            GroundTrajectory2DInfo groundTrajectory2DInfo = findFastestGroundTrajectory2DInfo(
                    position,
                    destination,
                    desiredSpeed);

            double speedFound = groundTrajectory2DInfo.findAverageSpeed(currentTestTime);
            if(speedFound < desiredSpeed) {
                //System.out.println(speedFound);
                return groundTrajectory2DInfo;
            }
        }

        return new GroundTrajectory2DInfo();
    }
}
