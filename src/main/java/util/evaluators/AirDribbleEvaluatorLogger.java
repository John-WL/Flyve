package util.evaluators;

import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.input.dynamic_data.DataPacket;
import util.parameter_configuration.data.handler.DataHandler;
import util.parameter_configuration.data.handler.FileParameter;
import util.parameter_configuration.data.handler.LogFileParameter;
import util.parameter_configuration.data.handler.SafeLineIncrementFileParameter;
import util.parameter_configuration.data.representation.DataRepresentation;

import java.util.List;

public class AirDribbleEvaluatorLogger extends BotEvaluator {

    private FileParameter fileParameter;

    public AirDribbleEvaluatorLogger(FileParameter fileParameter, CarDestination desiredDestination) {
        super(desiredDestination);
        this.fileParameter = fileParameter;
    }

    @Override
    public void updateEvaluation(DataPacket input) {
        // gotta find a way to evaluate air dribbles
        double currentEvaluation = 0;

        // this is where we judge intensely the bot
        currentEvaluation -= input.ball.position.minus(input.car.position).magnitude();
        currentEvaluation += 2*input.ball.position.z;
        currentEvaluation -= 4*getDesiredDestination().getThrottleDestination().minus(input.ball.position).magnitude();

        // apply the new evaluation
        setEvaluation(currentEvaluation/1000 + getEvaluation());
    }

    @Override
    public void resetEvaluation() {
        // serialize data so we can visualize it later
        fileParameter.set(getEvaluation());

        // do the things we normally do with superclass implementation
        super.resetEvaluation();
    }
}
