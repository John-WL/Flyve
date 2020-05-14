package rlbotexample.bot_behaviour.panbot.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugFuturePlayerHitBox;
import rlbotexample.bot_behaviour.panbot.debug.player_values.DebugPlayerHitBox;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.HitBox;
import rlbotexample.input.dynamic_data.Orientation;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

import java.awt.*;

public class DebugPredictedAerialHitOnBall extends PanBot {

    private Predictions predictions;
    private DebugPlayerPredictedTrajectory debugPlayerPredictedTrajectory;
    private DebugPlayerHitBox debugPlayerHitBox;
    private DebugFuturePlayerHitBox debugFuturePlayerHitBox;

    public DebugPredictedAerialHitOnBall() {
        this.predictions = new Predictions();
        this.debugPlayerPredictedTrajectory = new DebugPlayerPredictedTrajectory();
        this.debugPlayerHitBox = new DebugPlayerHitBox();
        this.debugFuturePlayerHitBox = new DebugFuturePlayerHitBox();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {

        debugPlayerPredictedTrajectory.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        debugPlayerHitBox.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        double timeOfIntersection = predictions.findIntersectionTimeBetweenAerialPlayerPositionAndBall(input.car, input.ball);
        if(timeOfIntersection < 6) {
            Orientation playerOrientation = new Orientation(input.car.orientation.noseVector, input.car.orientation.roofVector);
            HitBox playerHitBox = input.car.hitBox;
            Vector3 futurePlayerPosition = predictions.aerialKinematicBody(input.car.position, input.car.velocity, timeOfIntersection).getPosition();
            Vector3 futureBallPosition = predictions.getNativeBallPrediction(input.ball.position, timeOfIntersection).getPosition();

            HitBox futurePlayerHitBox = playerHitBox.generateHypotheticalHitBox(futurePlayerPosition, playerOrientation);
            Vector3 hitPoint = futurePlayerHitBox.projectPointOnSurface(futureBallPosition);
            renderer.drawLine3d(Color.CYAN, hitPoint.plus(new Vector3(20, 20, 20)), hitPoint.plus(new Vector3(-20, -20, -20)));
            renderer.drawLine3d(Color.CYAN, hitPoint.plus(new Vector3(-20, 20, 20)), hitPoint.plus(new Vector3(20, -20, -20)));
            renderer.drawLine3d(Color.CYAN, hitPoint.plus(new Vector3(20, -20, 20)), hitPoint.plus(new Vector3(-20, 20, -20)));
            renderer.drawLine3d(Color.CYAN, hitPoint.plus(new Vector3(20, 20, -20)), hitPoint.plus(new Vector3(-20, -20, 20)));
            debugFuturePlayerHitBox.setHitBox(futurePlayerHitBox);
            debugFuturePlayerHitBox.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        }

        Vector3 previousBallPosition = input.ball.position;
        int resolution = 30;
        for(int i = 0; i < resolution; i++) {
            double secondsInTheFuture = (6.0*i)/resolution;
            Vector3 futureBallPosition = predictions.resultingBallTrajectoryFromAerialHit(input.car, input.ball, secondsInTheFuture).getPosition();
            renderer.drawLine3d(Color.red, previousBallPosition, futureBallPosition);
            previousBallPosition = futureBallPosition;
        }
    }
}
