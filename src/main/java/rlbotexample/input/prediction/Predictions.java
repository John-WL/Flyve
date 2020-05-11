package rlbotexample.input.prediction;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbot.flat.Physics;
import rlbot.flat.PredictionSlice;
import util.game_constants.RlConstants;
import util.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Predictions {

    private final List<KinematicPoint> loadedBallPath = new ArrayList<>();

    public KinematicPoint aerialKinematicBody(Vector3 playerPosition, Vector3 playerSpeed, double secondsInTheFuture) {
        /* position prediction */
        // prediction in X
        double futureXPosition = playerSpeed.x * secondsInTheFuture;
        futureXPosition += playerPosition.x;

        // prediction in Y
        double futureYPosition = playerSpeed.y * secondsInTheFuture;
        futureYPosition += playerPosition.y;

        // prediction in Z
        double futureZPosition = -RlConstants.NORMAL_GRAVITY_STRENGTH/2 * secondsInTheFuture * secondsInTheFuture;
        futureZPosition += playerSpeed.z * secondsInTheFuture;
        futureZPosition += playerPosition.z;

        Vector3 futurePosition = new Vector3(futureXPosition, futureYPosition, futureZPosition);

        /* speed prediction */
        // prediction in X
        double futureXSpeed = playerSpeed.x;

        // prediction in Y
        double futureYSpeed = playerSpeed.y;

        // prediction in Z
        double futureZSpeed = -RlConstants.NORMAL_GRAVITY_STRENGTH * secondsInTheFuture;
        futureZSpeed += playerSpeed.z;

        Vector3 futureSpeed = new Vector3(futureXSpeed, futureYSpeed, futureZSpeed);

        return new KinematicPoint(futurePosition, futureSpeed, secondsInTheFuture);
    }

    public KinematicPoint onGroundKinematicBody(Vector3 playerPosition, Vector3 previousPlayerPosition, Vector3 playerSpeed, double secondsInTheFuture) {
        /* finding some parameters */
        Vector3 circleTangent = playerSpeed.scaledToMagnitude(1);

        // finding the direction in which the center of the circle is situated with respect to the speed vector
        double centerDirectionWithRespectToSpeedVector;
        if(previousPlayerPosition.minus(playerPosition).minusAngle(playerSpeed).y > 0) {
            centerDirectionWithRespectToSpeedVector = 1;
        }
        else {
            centerDirectionWithRespectToSpeedVector = -1;
        }

        Vector3 perpendicularToTangentSegment = circleTangent.plusAngle(new Vector3(0, centerDirectionWithRespectToSpeedVector, 0));

        double distanceBetweenPreviousAndCurrentPosition = playerPosition.minus(previousPlayerPosition).magnitude();

        

        /* position prediction */
        // prediction in X
        double futureXPosition =
        futureXPosition += playerPosition.x;

        // prediction in Y
        double futureYPosition = playerSpeed.y * secondsInTheFuture;
        futureYPosition += playerPosition.y;

        // prediction in Z
        double futureZPosition = -RlConstants.NORMAL_GRAVITY_STRENGTH/2 * secondsInTheFuture * secondsInTheFuture;
        futureZPosition += playerSpeed.z * secondsInTheFuture;
        futureZPosition += playerPosition.z;

        Vector3 futurePosition = new Vector3(futureXPosition, futureYPosition, futureZPosition);

        /* speed prediction */
        // prediction in X
        double futureXSpeed = playerSpeed.x;

        // prediction in Y
        double futureYSpeed = playerSpeed.y;

        // prediction in Z
        double futureZSpeed = -RlConstants.NORMAL_GRAVITY_STRENGTH * secondsInTheFuture;
        futureZSpeed += playerSpeed.z;

        Vector3 futureSpeed = new Vector3(futureXSpeed, futureYSpeed, futureZSpeed);

        return new KinematicPoint(futurePosition, futureSpeed, secondsInTheFuture);
    }

    public double timeBeforeReachingAerialPlayerApogeePosition(Vector3 playerSpeed) {
        return playerSpeed.z/RlConstants.NORMAL_GRAVITY_STRENGTH;
    }

    public void loadBallPrediction() {
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

    public KinematicPoint ball(Vector3 ballPosition, double secondsInTheFuture) {
        KinematicPoint futureBall = null;
        if (loadedBallPath.size() > 0) {
            futureBall = loadedBallPath.get(0);
        }

        if(futureBall == null) {
            return new KinematicPoint(new Vector3(), new Vector3(), 0);
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

    public double findIntersectionTimeBetweenAerialPlayerPositionAndBall(Vector3 playerPosition, Vector3 playerSpeed, Vector3 ballPosition, Vector3 ballSpeed) {
        // assume we don't have an intersection and that it takes
        // virtually an infinite amount of time to reach that point
        double timeOfImpact = Double.MAX_VALUE;

        // make sure we compute on a not empty array
        if(loadedBallPath.size() == 0) {
            return timeToReachAerialDestination(playerPosition.minus(ballPosition), playerSpeed.minus(ballSpeed));
        }

        // find the next time we'll hit the ball
        double bestPlayerDistanceFromBall = Double.MAX_VALUE;
        for(int i = 0; i < loadedBallPath.size(); i++) {
            double secondsInTheFuture = 6.0*(((double)i)/loadedBallPath.size());
            Vector3 futureBallPosition = ball(ballPosition, secondsInTheFuture).getPosition();
            Vector3 futurePlayerPosition = aerialKinematicBody(playerPosition, playerSpeed, secondsInTheFuture).getPosition();
            double futurePlayerDistanceFromFutureBall = futureBallPosition.minus(futurePlayerPosition).magnitude();

            if(bestPlayerDistanceFromBall > futurePlayerDistanceFromFutureBall) {
                bestPlayerDistanceFromBall = futurePlayerDistanceFromFutureBall;
            }

            if(bestPlayerDistanceFromBall < RlConstants.BALL_RADIUS + 50) {
                timeOfImpact = secondsInTheFuture;
                break;
            }
        }

        return timeOfImpact;
    }

    public KinematicPoint resultingBallTrajectoryFromAerialHit(Vector3 playerPosition, Vector3 playerSpeed, Vector3 ballPosition, Vector3 ballSpeed, double secondsInTheFuture) {
        double timeOfImpact = findIntersectionTimeBetweenAerialPlayerPositionAndBall(playerPosition, playerSpeed, ballPosition, ballSpeed);

        // if we're not hitting the ball at all, or if we're hitting the ball before the predicted impact
        if(timeOfImpact > 6 || timeOfImpact > secondsInTheFuture) {
            return ball(ballPosition, secondsInTheFuture);
        }
        else {
            KinematicPoint futureBall = ball(ballPosition, timeOfImpact);
            KinematicPoint futurePlayer = aerialKinematicBody(playerPosition, playerSpeed, timeOfImpact);
            Vector3 futureBallPosition = futureBall.getPosition();
            Vector3 futurePlayerPosition = futurePlayer.getPosition();
            Vector3 futureBallSpeed = futureBall.getSpeed();
            Vector3 futurePlayerSpeed = futurePlayer.getSpeed();

            // this is hard to compute, and so this is only an approximation for now...
            // get the normal vector of the hit so we can do stuff with it.
            Vector3 hitNormal = futureBallPosition.minus(futurePlayerPosition).scaledToMagnitude(1);

            // find the appropriate vector length that's gonna help us find the resulting change in direction from the hit normal
            double suddenChangeInSpeedAmount = 2*futureBallSpeed.dotProduct(hitNormal);

            // flip the future ball speed in the direction perpendicular to the normal on the perpendicular plane of the normal
            Vector3 flippedBallSpeed = futureBallSpeed.plus(hitNormal.scaledToMagnitude(suddenChangeInSpeedAmount));

            // add the player's speed difference from the ball to find the result of the hit
            Vector3 predictedBallSpeed = futurePlayerSpeed.minus(futureBallSpeed).plus(flippedBallSpeed);

            return aerialKinematicBody(futureBallPosition, predictedBallSpeed, secondsInTheFuture - timeOfImpact);
        }
    }
}
