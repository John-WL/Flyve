package rlbotexample.bot_behaviour.panbot.debug.player_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.ExtendedCarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

import java.awt.*;

public class DebugPlayerPredictedTrajectory extends PanBot {

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

        // get the car
        ExtendedCarData playerCar = input.car;

        // draw predicted parabola
        int resolution = 30;
        double amountOfTimeInFuture = 3;
        Vector3 previousPredictedPosition = playerCar.position;

        if(playerCar.position.z < 20 && playerCar.hasWheelContact) {
            // draw predicted ground trajectory
            for(int i = 1; i < resolution; i++) {
                // get the next predicted position
                double secondsFromNow = (i*amountOfTimeInFuture)/resolution;
                Vector3 predictedPosition = predictions.onGroundKinematicBody(playerCar, secondsFromNow).getPosition();

                // print a small segment on the trajectory
                renderer.drawLine3d(Color.ORANGE, previousPredictedPosition, predictedPosition);

                // setup for the next prediction so we can draw the next segment
                previousPredictedPosition = predictedPosition;
            }
        }
        else {
            for(int i = 1; i < resolution; i++) {
                // get the next predicted position
                double secondsFromNow = (i*amountOfTimeInFuture)/resolution;
                Vector3 predictedPosition = predictions.aerialKinematicBody(playerCar.position, playerCar.velocity, secondsFromNow).getPosition();

                // print a small segment on the trajectory
                renderer.drawLine3d(Color.ORANGE, previousPredictedPosition, predictedPosition);

                // setup for the next prediction so we can draw the next segment
                previousPredictedPosition = predictedPosition;
            }
        }

        // try to predict the point with which we should try to hit the getNativeBallPrediction
        double timeBeforeReachingBall = predictions.timeToReachAerialDestination(input.ball.position.minus(playerCar.position), input.ball.velocity.minus(playerCar.velocity));
        renderer.drawRectangle3d(Color.CYAN, predictions.aerialKinematicBody(playerCar.position, playerCar.velocity, timeBeforeReachingBall).getPosition(), 10, 10, true);

        // draw find the same timed point, but on the getNativeBallPrediction trajectory instead
        renderer.drawRectangle3d(Color.green, predictions.getNativeBallPrediction(input.ball.position, timeBeforeReachingBall).getPosition(), 10, 10, true);
    }
}
