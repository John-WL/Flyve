package util.controllers;

import util.game_constants.RlConstants;

public class BoostController {

    private double currentDeltaV;

    public BoostController() {
        this.currentDeltaV = 0;
    }

    public boolean process(double desiredAverageAcceleration) {
        currentDeltaV += desiredAverageAcceleration;
        if(currentDeltaV > RlConstants.ACCELERATION_DUE_TO_BOOST) {
            currentDeltaV -= RlConstants.ACCELERATION_DUE_TO_BOOST;
            return true;
        }

        return false;
    }
}
