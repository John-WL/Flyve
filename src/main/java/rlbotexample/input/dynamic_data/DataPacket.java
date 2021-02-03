package rlbotexample.input.dynamic_data;

import rlbot.flat.GameTickPacket;
import rlbotexample.input.dynamic_data.boost.BoostManager;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.prediction.gamestate_prediction.GameStatePrediction;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is here for your convenience, it is NOT part of the framework. You can change it as much
 * as you want, or delete it. The benefits of using this instead of rlbot.flat.GameTickPacket are:
 * 1. You end up with nice custom Vector3 objects that you can call methods on.
 * 2. If the framework changes its data format, you can just update the code here
 * and leave your bot logic alone.
 */
public class DataPacket {

    /** Your own car, based on the playerIndex */
    public final ExtendedCarData car;

    public final List<ExtendedCarData> allCars;

    public final BallData ball;
    public final int team;

    public final GameStatePrediction statePrediction;

    /** The index of your player */
    public final int playerIndex;

    /** The index of the bot that is going to reload the ball prediction (if there is many bots) */
    public static final AtomicInteger indexOfBotThatReloadsPredictions = new AtomicInteger(-1);
    public static final AtomicInteger indexOfBotThatLoadsData = new AtomicInteger(-1);

    private static volatile boolean dataLoaded = false;

    public DataPacket(GameTickPacket request, int playerIndex) {
        this.playerIndex = playerIndex;
        synchronized (indexOfBotThatReloadsPredictions) {
            // indent to disable features
            if(indexOfBotThatLoadsData.get() == -1) {
                //indexOfBotThatReloadsPredictions.set(playerIndex);
            }
            if(indexOfBotThatLoadsData.get() == -1) {
                indexOfBotThatLoadsData.set(playerIndex);
            }
        }
        this.allCars = new ArrayList<>();
        List<CarData> carsForGameStatePrediction = new ArrayList<>();
        for (int i = 0; i < request.playersLength(); i++) {
            final rlbot.flat.PlayerInfo playerInfo = request.players(i);
            final float elapsedSeconds = request.gameInfo().secondsElapsed();
            allCars.add(new ExtendedCarData(playerInfo, i, elapsedSeconds));
            carsForGameStatePrediction.add(new CarData(request.players(i), request.gameInfo().secondsElapsed()));
        }
        this.car = allCars.get(playerIndex);
        this.team = this.car.team;
        this.ball = new BallData(request.ball());
        this.statePrediction = RlUtils.gameStatePrediction(playerIndex, this.ball, carsForGameStatePrediction);

        // omg
        if(indexOfBotThatLoadsData.get() == playerIndex) {
            loadData(request);
            dataLoaded = true;
        }
        // omg kill me
        //noinspection StatementWithEmptyBody
        while(!dataLoaded);
    }

    private void loadData(GameTickPacket request) {
        // update the "raw" ball trajectory
        RawBallTrajectory.update(ball);

        // refresh boostPads information so we can utilize it
        BoostManager.loadGameTickPacket(request);
    }
}
