package util.evaluators;

import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.input.dynamic_data.DataPacket;

public class AirDribbleEvaluator extends BotEvaluator {

    public AirDribbleEvaluator(CarDestination desiredDestination) {
        super(desiredDestination);
    }

    @Override
    public void updateEvaluation(DataPacket input) {
        // gotta find a way to evaluate air dribbles

    }
}
