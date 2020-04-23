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
        double currentEvaluation = getEvaluation();

        currentEvaluation += 1000.0/input.ball.velocity.minus(input.car.velocity).magnitude();
        currentEvaluation += 1000.0/input.ball.position.flatten().minus(input.car.position.flatten()).magnitude();

        setEvaluation(currentEvaluation);
    }
}
