package rlbotexample.input.prediction.gamestate_prediction.ball;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

public class RawBallTrajectory {

    public static Trajectory3D trajectory;

    private static final int PREDICTION_REFRESH_RATE = 60;
    private static BallPrediction ballPrediction;

    public static void update() {
        try {
            ballPrediction = RLBotDll.getBallPrediction();
        } catch (RLBotInterfaceException e) {
            // assuming the ball isn't moving and is centered in the field
            trajectory = time -> new Vector3(0, 0, RlConstants.BALL_RADIUS);
        }
        trajectory = t -> new Vector3(ballPrediction.slices(correspondingBallIndex(t)).physics().location());
    }

    private static int correspondingBallIndex(final double deltaTime) {
        if((int) (PREDICTION_REFRESH_RATE * deltaTime) >= ballPrediction.slicesLength()) {
            return ballPrediction.slicesLength() - 1;
        }
        else if((int) (PREDICTION_REFRESH_RATE * deltaTime) < 0) {
            return 0;
        }
        return (int)(PREDICTION_REFRESH_RATE * deltaTime);
    }
}
