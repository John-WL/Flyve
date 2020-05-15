package rlbotexample.bot_behaviour.panbot.debug.ball_prediction;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.BallData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.BallBounce;
import rlbotexample.input.prediction.BallPredictionHelper;
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

        // custom ball prediction is purple
        // native one is red
        BallData previousBall = input.ball;
        int divisor = 0;
        for(BallData nextBall: input.ballPrediction.balls) {
            divisor++;
            divisor %= 4;

            if(divisor == 0) {
                renderer.drawLine3d(new Color(158, 63, 229), previousBall.position, nextBall.position);
                previousBall = nextBall;
            }
        }

        renderer.drawLine3d(Color.green, new Vector3(4096, 5120-1152, 500), new Vector3(4096-800, 5120-1152-400, 500));

        /*
        try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            BallPredictionHelper.drawTillMoment(ballPrediction, input.car.elapsedSeconds + 6, Color.red, renderer);
        } catch (RLBotInterfaceException e) {
            e.printStackTrace();
        }*/



        /*
        BallData initialBall = new BallData(new Vector3(), new Vector3(2800, 0, -1616), new Vector3(0, 0, 0), 0);
        System.out.println("bounce 0: " + initialBall.surfaceVelocity(new Vector3(0, 0, -1)));

        BallData bouncedBall = new BallBounce(initialBall, new Vector3(0, 0, -1)).compute();
        System.out.println("bounce 1: " + bouncedBall.surfaceVelocity(new Vector3(0, 0, -1)));

        BallData secondTimeBouncedBall = new BallBounce(bouncedBall, new Vector3(0, 0, -1)).compute();
        System.out.println("bounce 2: " + secondTimeBouncedBall.surfaceVelocity(new Vector3(0, 0, -1)));
        */
    }
}
