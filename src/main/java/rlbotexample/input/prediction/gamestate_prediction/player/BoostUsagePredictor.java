package rlbotexample.input.prediction.gamestate_prediction.player;

import util.game_constants.RlConstants;
import util.math.MovingAverage;

public class BoostUsagePredictor {
    private MovingAverage usageAverage;

    public BoostUsagePredictor() {
        int amountOfFramesForTheAverage = (int)(RlConstants.BOT_REFRESH_RATE*0.2);
        usageAverage = new MovingAverage(amountOfFramesForTheAverage);
    }

    public void update(boolean isBoosting) {
        usageAverage.update(isBoosting ? 1 : 0);
    }
}
