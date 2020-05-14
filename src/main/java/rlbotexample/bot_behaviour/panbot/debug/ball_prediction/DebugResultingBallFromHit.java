package rlbotexample.bot_behaviour.panbot.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_situation.ball_hit.*;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import util.vector.Vector3;

import java.awt.*;

public class DebugResultingBallFromHit extends PanBot {

    private Predictions predictions;
    private GameSituationHandler gameSituationHandler;

    public DebugResultingBallFromHit() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new CeilingHit1());
        gameSituationHandler.add(new CeilingHit2());
        gameSituationHandler.add(new CeilingHit3());
        gameSituationHandler.add(new CeilingHit4());
        gameSituationHandler.add(new CeilingHit5());
        gameSituationHandler.add(new CeilingHit6());
        this.predictions = new Predictions();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situation handling
        gameSituationHandler.update();
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        Vector3 lastPosition = input.ball.position;
        int precision = 200;
        for(int i = 0; i < precision; i++) {
            Vector3 newPosition = predictions.ballPredictionRoughMapEstimateBounce(input.ball.position, input.ball.velocity, input.ball.spin, 2.0*i/precision).getPosition();
            renderer.drawLine3d(Color.red, lastPosition, newPosition);
            lastPosition = newPosition;
        }

        //KinematicPoint bouncedBall = predictions.resultingBallFromHit(new Vector3(), new Vector3(0, 0, 100), new Vector3(0, 100, 0), new Vector3(0, 0, 1));

        //System.out.println(bouncedBall.getSpeed());
    }
}
