package rlbotexample.bot_behaviour.panbot.debug;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

import java.awt.*;

public class DebugPredictedAerialHitOnBall extends PanBot {

    Predictions predictions;

    public DebugPredictedAerialHitOnBall() {
        this.predictions = new Predictions();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        predictions.loadBallPrediction();

        double timeOfIntersection = predictions.findIntersectionTimeBetweenAerialPlayerPositionAndBall(input.allCars.get(1-input.playerIndex).position, input.allCars.get(1-input.playerIndex).velocity, input.ball.position, input.ball.velocity);

        if(timeOfIntersection < 6) {
            Vector3 futurePlayerPosition = predictions.aerialKinematicBody(input.allCars.get(1-input.playerIndex).position, input.allCars.get(1-input.playerIndex).velocity, timeOfIntersection).getPosition();
            renderer.drawLine3d(Color.CYAN, futurePlayerPosition.plus(new Vector3(20, 20, 20)), futurePlayerPosition.plus(new Vector3(-20, -20, -20)));
            renderer.drawLine3d(Color.CYAN, futurePlayerPosition.plus(new Vector3(-20, 20, 20)), futurePlayerPosition.plus(new Vector3(20, -20, -20)));
            renderer.drawLine3d(Color.CYAN, futurePlayerPosition.plus(new Vector3(20, -20, 20)), futurePlayerPosition.plus(new Vector3(-20, 20, -20)));
            renderer.drawLine3d(Color.CYAN, futurePlayerPosition.plus(new Vector3(20, 20, -20)), futurePlayerPosition.plus(new Vector3(-20, -20, 20)));
        }

        Vector3 previousBallPosition = input.ball.position;
        int resolution = 200;
        for(int i = 0; i < resolution; i++) {
            Vector3 futureBallPosition = predictions.resultingBallTrajectoryFromAerialHit(input.allCars.get(1).position, input.allCars.get(1).velocity, input.ball.position, input.ball.velocity, (6.0*i)/resolution).getPosition();
            renderer.drawLine3d(Color.red, previousBallPosition, futureBallPosition);
            previousBallPosition = futureBallPosition;
        }
    }
}
