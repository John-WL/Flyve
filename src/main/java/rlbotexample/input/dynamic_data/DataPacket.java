package rlbotexample.input.dynamic_data;

import rlbot.flat.GameTickPacket;
import rlbotexample.input.prediction.AdvancedBallPrediction;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is here for your convenience, it is NOT part of the framework. You can change it as much
 * as you want, or delete it. The benefits of using this instead of rlbot.flat.GameTickPacket are:
 * 1. You end up with nice custom Vector3 objects that you can call methods on.
 * 2. If the framework changes its data format, you can just update the code here
 * and leave your bot logic alone.
 */
public class DataPacket {

    public static final double BALL_PREDICTION_TIME = 6;
    public static final double BALL_PREDICTION_REFRESH_RATE = 120;

    /** Your own car, based on the playerIndex */
    public final CarData car;

    public final List<CarData> allCars;

    public final BallData ball;
    public final int team;

    public final AdvancedBallPrediction ballPrediction;

    /** The index of your player */
    public final int playerIndex;

    public DataPacket(GameTickPacket request, int playerIndex) {
        this.playerIndex = playerIndex;
        this.allCars = new ArrayList<>();
        for (int i = 0; i < request.playersLength(); i++) {
            allCars.add(new CarData(request.players(i), request.gameInfo().secondsElapsed()));
        }
        this.car = allCars.get(playerIndex);
        this.team = this.car.team;
        this.ball = new BallData(request.ball());
        this.ballPrediction = new AdvancedBallPrediction(this.ball, this.allCars, BALL_PREDICTION_TIME, BALL_PREDICTION_REFRESH_RATE);
    }
}
