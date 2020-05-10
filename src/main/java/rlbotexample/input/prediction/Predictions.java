package rlbotexample.input.prediction;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbot.flat.PredictionSlice;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.vector.Vector3;

public class Predictions {

    public static Vector3 aerialPlayerPosition(Vector3 playerPosition, Vector3 playerSpeed, double secondsInTheFuture) {
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

        return new Vector3(futureXPosition, futureYPosition, futureZPosition);
    }

    public static double timeBeforeReachingAerialPlayerApogeePosition(Vector3 playerSpeed) {
        return playerSpeed.z/RlConstants.NORMAL_GRAVITY_STRENGTH;
    }

    public static Vector3 ballPositon(Vector3 ballPosition, double secondsInTheFuture) {
        Vector3 futureBallPosition;

        try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            PredictionSlice predictedBall = ballPrediction.slices(0);
            double initialTime = predictedBall.gameSeconds();
            int i = 0;
            while(i < ballPrediction.slicesLength()) {
                if(ballPrediction.slices(i).gameSeconds() - initialTime < secondsInTheFuture) {
                    predictedBall = ballPrediction.slices(i);
                }
                else {
                    // UGH this is ugly
                    break;
                }
                i++;
            }
            futureBallPosition = new Vector3(predictedBall.physics().location());
        } catch (RLBotInterfaceException e) {
            e.printStackTrace();
            futureBallPosition = ballPosition;
        }

        return futureBallPosition;
    }
}
