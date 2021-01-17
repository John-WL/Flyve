package rlbotexample.bot_behaviour.flyve.debug.player_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class DebugPlayerPredictedTrajectory extends FlyveBot {

    private Predictions predictions;

    public DebugPlayerPredictedTrajectory() {
        this.predictions = new Predictions();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        int resolution = 30;
        Vector3 previousPosition = input.allCars.get(input.playerIndex).position;
        for(int i = 0; i < resolution; i++) {
            Vector3 nextPosition = input.statePrediction.carsAtTime(i/5.0).get(input.playerIndex).position;
            renderer.drawLine3d(Color.YELLOW, previousPosition, nextPosition);
            previousPosition = nextPosition;
        }
    }
}
