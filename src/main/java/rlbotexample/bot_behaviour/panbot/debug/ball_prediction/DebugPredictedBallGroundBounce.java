package rlbotexample.bot_behaviour.panbot.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class DebugPredictedBallGroundBounce extends PanBot {

    private Predictions predictions;

    public DebugPredictedBallGroundBounce() {
        this.predictions = new Predictions();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        Vector3 previousPosition = input.ball.position;
        int resolution = 200;
        for(int i = 0; i < resolution; i++) {
            Vector3 predictedPosition = predictions.ballPredictionGroundBounce(input.ball.position, input.ball.velocity, input.ball.spin, 6.0*i/resolution).getPosition();
            renderer.drawLine3d(Color.red, predictedPosition, previousPosition);
            previousPosition = predictedPosition;
        }

        //Vector3 predictedPosition = predictions.ballPredictionGroundBounce(input.ball.position, input.ball.velocity, input.ball.spin, 1).getPosition();
        //renderer.drawLine3d(Color.red, predictedPosition, previousPosition);
    }
}
