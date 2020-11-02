package rlbotexample.input.dynamic_data;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.ball.AdvancedBallPrediction;
import util.game_constants.RlConstants;
import util.timer.Timer;
import util.math.vector.Vector3;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RlUtils {

    public static final double BALL_PREDICTION_TIME = 5;
    public static final double BALL_PREDICTION_REFRESH_RATE = 120;

    private static Timer ballPredictionReloadTimeout = new Timer(0).start();
    private static AdvancedBallPrediction ballPrediction = new AdvancedBallPrediction(new BallData(new Vector3(), new Vector3(), new Vector3(), 0), new ArrayList<CarData>(), 0, BALL_PREDICTION_REFRESH_RATE);

    private static final List<Integer> amountOfFramesSinceFirstJumpOccurredForAllPlayers = new ArrayList<>();

    public static AdvancedBallPrediction ballPrediction(int playerIndex, BallData ballData, List<CarData> allCars) {
        if(playerIndex == 0 && ballPredictionReloadTimeout.isTimeElapsed()) {
            ballPredictionReloadTimeout = new Timer(1.0/RlConstants.BOT_REFRESH_RATE).start();
            ballPrediction = new AdvancedBallPrediction(ballData, allCars, BALL_PREDICTION_TIME, BALL_PREDICTION_REFRESH_RATE);
        }
        return ballPrediction;
    }

    public static int getPreviousAmountOfFramesSinceFirstJumpOccurred(int playerIndex) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                return amountOfFramesSinceFirstJumpOccurredForAllPlayers.get(playerIndex);
            } catch (Exception e) {
                amountOfFramesSinceFirstJumpOccurredForAllPlayers.add(0);
            }
        }
        throw new RuntimeException();
    }

    public static void setPreviousAmountOfFramesSinceFirstJumpOccurred(int playerIndex, int framesSinceFirstJumpOccurred) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                amountOfFramesSinceFirstJumpOccurredForAllPlayers.set(playerIndex, framesSinceFirstJumpOccurred);
                return;
            } catch (Exception e) {
                amountOfFramesSinceFirstJumpOccurredForAllPlayers.add(0);
            }
        }
        throw new RuntimeException();
    }

    // good enough approximation of time before aerial hit for now.
    public static double timeToReachAerialDestination(DataPacket input, Vector3 playerDistanceFromDestination, Vector3 playerSpeedFromDestination) {
        // this is the player speed SIGNED (it's the player speed, but it's negative if it's going away from the destination...)
        double signedPlayerSpeedFromBall = playerSpeedFromDestination.dotProduct(playerDistanceFromDestination)
                / playerDistanceFromDestination.magnitude();
        double a = -(RlConstants.ACCELERATION_DUE_TO_BOOST)/3.5 /*+ (input.car.orientation.noseVector.dotProduct(new Vector3(0, 0, 1))*RlConstants.NORMAL_GRAVITY_STRENGTH/2)*/;
        double b = signedPlayerSpeedFromBall;
        double c = playerDistanceFromDestination.magnitude();
        double timeBeforeReachingBall = -b - Math.sqrt(b*b - 4*a*c);
        timeBeforeReachingBall /= 2*a;

        // player never has more than 3 seconds to boost in air, so we cap it here.
        // not sure if this is necessary though. It works fine with it
        /*if(timeBeforeReachingBall > 3) {
            timeBeforeReachingBall = 3;
        }*/

        return timeBeforeReachingBall;
    }

    public static Vector3 findConstantAccelerationNeededToReachAerialDestination(ExtendedCarData carData, Vector3 xfVec, double t) {
        // variable name conversion so that the variable names in my notebook match, please ignore
        Vector3 xiVec = carData.position;
        Vector3 viVec = carData.velocity;

        double ax = findAcceleration(xiVec.x, xfVec.x, viVec.x, t);
        double ay = findAcceleration(xiVec.y, xfVec.y, viVec.y, t);
        double az = findAcceleration(xiVec.z, xfVec.z, viVec.z, t);

        return new Vector3(ax, ay, az);
    }

    private static double findAcceleration(double xi, double xf, double vi, double t) {
        return 2*(xf - xi - (vi*t))/(t*t);
    }

    private static double sq(double x) {
        return x*x;
    }

    public static Vector3 getOrientationForAerial(ExtendedCarData carData, Trajectory3D trajectory) {
        int precision = 120;
        double amountOfTimeToSearch = 5;
        for(int i = 1; i < precision*amountOfTimeToSearch; i++) {
            double currentTestTime = (i/(double)precision)*amountOfTimeToSearch;
            Vector3 testAcceleration = RlUtils.findConstantAccelerationNeededToReachAerialDestination(carData, trajectory.compute(currentTestTime), currentTestTime);
            if(testAcceleration.plus(new Vector3(0, 0, RlConstants.NORMAL_GRAVITY_STRENGTH)).magnitude() < RlConstants.ACCELERATION_DUE_TO_BOOST) {
                return testAcceleration.plus(new Vector3(0, 0, RlConstants.NORMAL_GRAVITY_STRENGTH));
            }
        }

        return new Vector3();
    }
}
