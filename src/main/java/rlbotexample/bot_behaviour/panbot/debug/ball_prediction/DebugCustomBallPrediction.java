package rlbotexample.bot_behaviour.panbot.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.BallData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

import java.awt.*;

public class DebugCustomBallPrediction extends PanBot {

    public DebugCustomBallPrediction() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        BallData previousBall = input.ball;
        int divisor = 0;
        for(BallData nextBall: input.ballPrediction.balls) {
            divisor++;
            divisor %= 12;

            if(divisor == 0) {
                renderer.drawLine3d(new Color(158, 63, 229), previousBall.position, nextBall.position);
                previousBall = nextBall;
            }
        }
    }
}
