package rlbotexample.input.prediction;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbot.flat.Physics;
import rlbot.flat.PredictionSlice;
import rlbotexample.input.dynamic_data.BallData;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.dynamic_data.CarOrientation;
import rlbotexample.input.dynamic_data.HitBox;
import util.game_constants.RlConstants;
import util.vector.Vector2;
import util.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Predictions {

    private final List<KinematicPoint> loadedBallPath = new ArrayList<>();

    public Predictions() {}

    // this is just a parabola
    public KinematicPoint aerialKinematicBody(Vector3 kinematicBodyPosition, Vector3 kinematicBodySpeed, double secondsInTheFuture) {

        /* position prediction */
        // prediction in X
        double futureXPosition = kinematicBodySpeed.x * secondsInTheFuture;
        futureXPosition += kinematicBodyPosition.x;

        // prediction in Y
        double futureYPosition = kinematicBodySpeed.y * secondsInTheFuture;
        futureYPosition += kinematicBodyPosition.y;

        // prediction in Z
        double futureZPosition = -RlConstants.NORMAL_GRAVITY_STRENGTH/2 * secondsInTheFuture * secondsInTheFuture;
        futureZPosition += kinematicBodySpeed.z * secondsInTheFuture;
        futureZPosition += kinematicBodyPosition.z;

        Vector3 futurePosition = new Vector3(futureXPosition, futureYPosition, futureZPosition);

        /* speed prediction */
        // prediction in X
        double futureXSpeed = kinematicBodySpeed.x;

        // prediction in Y
        double futureYSpeed = kinematicBodySpeed.y;

        // prediction in Z
        double futureZSpeed = -RlConstants.NORMAL_GRAVITY_STRENGTH * secondsInTheFuture;
        futureZSpeed += kinematicBodySpeed.z;

        Vector3 futureSpeed = new Vector3(futureXSpeed, futureYSpeed, futureZSpeed);

        return new KinematicPoint(futurePosition, futureSpeed, secondsInTheFuture);
    }

    // this is the circle path that the player is drawing while driving on ground
    public KinematicPoint onGroundKinematicBody(CarData carData, double secondsInTheFuture) {
        Vector3 playerPosition = carData.position;
        Vector3 playerSpeed = carData.velocity;
        Vector3 playerNoseOrientation = carData.orientation.noseVector;
        Vector3 playerRoofOrientation = carData.orientation.roofVector;
        Orientation playerOrientation = new Orientation(playerNoseOrientation, playerRoofOrientation);
        Vector3 playerSpin = carData.spin;
        double spinAmount = -playerSpin.z;

        if(Math.abs(spinAmount) < 0.000001) {
            spinAmount = 0.000001;
        }

        double timeToDoOneRotation = 2*Math.PI/spinAmount;
        double circumference = timeToDoOneRotation * playerSpeed.magnitude();
        double radius = circumference/(2*Math.PI);

        double arcDistance = playerSpeed.magnitude()*secondsInTheFuture;
        double deltaRadiansOnCircle = arcDistance/radius;

        double deltaPositionXOnCircle = Math.cos(deltaRadiansOnCircle) * radius;
        double deltaPositionYOnCircle = Math.sin(deltaRadiansOnCircle) * radius;
        Vector3 initialDeltaPositionOnCircle = new Vector3(radius, 0, 0);
        Vector3 nextDeltaPositionOnCircle = new Vector3(deltaPositionXOnCircle, deltaPositionYOnCircle, 0);
        Vector3 nextPositionOnCenteredCircle = nextDeltaPositionOnCircle.minus(initialDeltaPositionOnCircle).plusAngle(playerSpeed).plusAngle(new Vector3(0, -1, 0));

        Vector3 futurePlayerPosition = nextPositionOnCenteredCircle.plus(playerPosition);
        Vector3 futurePlayerSpeed = playerSpeed;

        return new KinematicPoint(futurePlayerPosition, futurePlayerSpeed, secondsInTheFuture);
    }

    // if we don't load, the get function returns constantly the same path.
    // makes sure to load ONCE every frame.
    // not 2! Hey, I saw you. Sheesh.
    public void loadNativeBallPrediction() {
        loadedBallPath.clear();

        try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();

            for (int i = 0; i < ballPrediction.slicesLength(); i++) {

                PredictionSlice predictedBallSlice = ballPrediction.slices(i);
                Physics predictedBall = predictedBallSlice.physics();

                Vector3 position = new Vector3(predictedBall.location());
                Vector3 speed = new Vector3(predictedBall.velocity());
                double gameTime = predictedBallSlice.gameSeconds();

                loadedBallPath.add(new KinematicPoint(position, speed, gameTime));
            }
        }
        catch (RLBotInterfaceException e) {
            e.printStackTrace();
        }

        if (loadedBallPath.size() == 0) {
            loadedBallPath.add(new KinematicPoint(new Vector3(), new Vector3(), 0));
        }
    }

    // makes sure to load ONCE before getting the native ball prediction path.
    public KinematicPoint getNativeBallPrediction(Vector3 ballPosition, double secondsInTheFuture) {
        KinematicPoint futureBall = null;
        if (loadedBallPath.size() > 0) {
            futureBall = loadedBallPath.get(0);
        }

        if(futureBall == null) {
            return new KinematicPoint(ballPosition, new Vector3(), 0);
        }

        double initialTime = futureBall.getGameTime();
        int i = 0;
        while (i < loadedBallPath.size()) {
            KinematicPoint kinematicBall = loadedBallPath.get(i);

            // WTF why do they build null objects yamete
            if (kinematicBall == null) {
                break;
            }

            if (kinematicBall.getGameTime() - initialTime < secondsInTheFuture) {
                futureBall = kinematicBall;
            } else {
                // UGH this is ugly
                break;
            }
            i++;
        }

        return futureBall;
    }

    // good enough approximation of time before aerial hit for now.
    public double timeToReachAerialDestination(Vector3 playerDistanceFromDestination, Vector3 playerSpeedFromDestination) {
        // this is the player speed SIGNED (it's the player speed, but it's negative if it's going away from the destination...)
        double signedPlayerSpeedFromBall = playerSpeedFromDestination.dotProduct(playerDistanceFromDestination)
                / playerDistanceFromDestination.magnitude();
        double a = -RlConstants.ACCELERATION_DUE_TO_BOOST/2 /*+ (input.car.orientation.noseVector.dotProduct(new Vector3(0, 0, 1))*RlConstants.NORMAL_GRAVITY_STRENGTH/2)*/;
        double b = signedPlayerSpeedFromBall;
        double c = playerDistanceFromDestination.magnitude();
        double timeBeforeReachingBall = -b - Math.sqrt(b*b - 4*a*c);
        timeBeforeReachingBall /= 2*a;

        // player never has more than 3 seconds to boost in air, so we cap it here.
        // not sure if this is necessary though. It works fine with it
        if(timeBeforeReachingBall > 3) {
            timeBeforeReachingBall = 3;
        }

        return timeBeforeReachingBall;
    }

    // get the exact time it'll take before reaching the ball with respect to the current trajectory
    // (WHICH MEANS that a lot of times, it'll return infinity, duh (uh~ I mean Double.MAX_VALUE seconds...).
    // Path don't always intersect with each other, einstein).
    public double findIntersectionTimeBetweenAerialPlayerPositionAndBall(CarData carData, BallData ballData) {
        Vector3 playerPosition = carData.position;
        Vector3 playerSpeed = carData.velocity;
        HitBox playerHitBox = carData.hitBox;
        Vector3 ballPosition = ballData.position;
        Vector3 ballSpeed = ballData.velocity;

        // assume we don't have an intersection and that it takes
        // virtually an infinite amount of time to reach that point
        double timeOfImpact = Double.MAX_VALUE;

        // make sure we compute on a not empty array
        if(loadedBallPath.size() == 0) {
            return timeToReachAerialDestination(playerPosition.minus(ballPosition), playerSpeed.minus(ballSpeed));
        }

        // find the next time we'll hit the getNativeBallPrediction
        double bestPlayerDistanceFromBall = Double.MAX_VALUE;
        for(int i = 0; i < loadedBallPath.size(); i++) {
            double secondsInTheFuture = 6.0*(((double)i)/loadedBallPath.size());
            KinematicPoint futureBall = getNativeBallPrediction(ballPosition, secondsInTheFuture);
            KinematicPoint futurePlayer = aerialKinematicBody(playerPosition, playerSpeed, secondsInTheFuture);
            Vector3 futureBallPosition = futureBall.getPosition();
            Vector3 futurePlayerPosition = futurePlayer.getPosition();
            double futurePlayerDistanceFromFutureBall = futureBallPosition.minus(futurePlayerPosition).magnitude();

            if(bestPlayerDistanceFromBall > futurePlayerDistanceFromFutureBall) {
                bestPlayerDistanceFromBall = futurePlayerDistanceFromFutureBall;
            }
            double playerRadius = playerHitBox.projectPointOnSurface(futureBallPosition).minus(playerPosition).magnitude();
            if(bestPlayerDistanceFromBall < RlConstants.BALL_RADIUS + playerRadius) {
                timeOfImpact = secondsInTheFuture;
                break;
            }
        }

        return timeOfImpact;
    }

    public KinematicPoint resultingBallTrajectoryFromAerialHit(CarData carData, BallData ballData, double secondsInTheFuture) {
        Vector3 playerPosition = carData.position;
        Vector3 playerSpeed = carData.velocity;
        Vector3 ballPosition = ballData.position;

        double timeOfImpact = findIntersectionTimeBetweenAerialPlayerPositionAndBall(carData, ballData);

        // if we're not hitting the getNativeBallPrediction at all, or if we're hitting the getNativeBallPrediction before the predicted impact
        if(timeOfImpact > 6 || timeOfImpact > secondsInTheFuture) {
            return getNativeBallPrediction(ballPosition, secondsInTheFuture);
        }
        else {
            KinematicPoint futureBall = getNativeBallPrediction(ballPosition, timeOfImpact);
            KinematicPoint futurePlayer = aerialKinematicBody(playerPosition, playerSpeed, timeOfImpact);
            Vector3 futureBallPosition = futureBall.getPosition();
            Vector3 futurePlayerPosition = futurePlayer.getPosition();
            Vector3 futureBallSpeed = futureBall.getSpeed();
            Vector3 futurePlayerSpeed = futurePlayer.getSpeed();

            // this is hard to compute, and so this is only an approximation for now...
            // get the normal vector of the hit so we can flip the getNativeBallPrediction speed with respect to it
            Vector3 hitNormal = futureBallPosition.minus(futurePlayerPosition);

            // flip the future getNativeBallPrediction speed in the direction perpendicular to the normal
            Vector3 flippedBallSpeed = reverseSpeedFromCollisionNormal(futureBallSpeed, hitNormal);

            // add the player's speed difference from the getNativeBallPrediction to find the result of the hit
            Vector3 predictedBallSpeed = futurePlayerSpeed.minus(futureBallSpeed).plus(flippedBallSpeed);

            return aerialKinematicBody(futureBallPosition, predictedBallSpeed, secondsInTheFuture - timeOfImpact);
        }
    }

    public Orientation predictFutureOrientation(Orientation currentOrientation, Vector3 currentSpin, double timeInTheFuture) {
        Vector3 rotatedNoseOrientation = predictFutureOrientationVectorFromSpinAndTime(currentOrientation.getNose(), currentSpin, timeInTheFuture);
        Vector3 rotatedRoofOrientation = predictFutureOrientationVectorFromSpinAndTime(currentOrientation.getRoof(), currentSpin, timeInTheFuture);

        return new Orientation(rotatedNoseOrientation, rotatedRoofOrientation);

    }

    private Vector3 predictFutureOrientationVectorFromSpinAndTime(Vector3 currentOrientationVector, Vector3 currentSpin, double timeInTheFuture) {
        // negative because of the right hand rule
        double numberOfRadiansToDo = -currentSpin.magnitude()*timeInTheFuture;
        Vector3 local = currentOrientationVector.minusAngle(currentSpin);
        Vector2 localProjectionYz = new Vector2(local.y, local.z);
        Vector2 rotatoryVector = new Vector2(Math.cos(numberOfRadiansToDo), Math.sin(numberOfRadiansToDo));
        Vector2 localRotatedProjectionYz = localProjectionYz.plusAngle(rotatoryVector);
        Vector3 localRotated = new Vector3(local.x, localRotatedProjectionYz.x, localRotatedProjectionYz.y);

        return localRotated.plusAngle(currentSpin);
    }

    private Vector3 reverseSpeedFromCollisionNormal(Vector3 vectorToReverse, Vector3 hitNormal) {
        hitNormal = hitNormal.scaledToMagnitude(1);

        // find the appropriate vector length that's gonna help us find the resulting change in direction from the hit normal
        double suddenChangeInSpeedAmount = 2*vectorToReverse.dotProduct(hitNormal);

        // flip the future getNativeBallPrediction speed in the direction perpendicular to the normal on the perpendicular plane of the normal
        return vectorToReverse.plus(hitNormal.scaledToMagnitude(suddenChangeInSpeedAmount));
    }
}
