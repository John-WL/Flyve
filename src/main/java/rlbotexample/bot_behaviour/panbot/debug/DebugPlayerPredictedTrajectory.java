package rlbotexample.bot_behaviour.panbot.debug;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Orientation;
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
        CarData opponentCar = input.allCars.get((input.allCars.size()-1) - input.playerIndex);

        // draw predicted parabola
        int resolution = 30;
        double amountOfTimeInFuture = 6;
        Vector3 previousPredictedPosition = opponentCar.position;

        if(opponentCar.position.z > 50) {
            for(int i = 1; i < resolution; i++) {
                // get the next predicted position
                double secondsFromNow = (i*amountOfTimeInFuture)/resolution;
                Vector3 predictedPosition = predictions.aerialKinematicBody(opponentCar.position, opponentCar.velocity, secondsFromNow).getPosition();

                // print a small segment on the trajectory
                renderer.drawLine3d(Color.ORANGE, previousPredictedPosition, predictedPosition);

                // setup for the next prediction so we can draw the next segment
                previousPredictedPosition = predictedPosition;
            }
        }
        else {
            // draw predicted ground trajectory
            for(int i = 1; i < resolution; i++) {
                // get the next predicted position
                double secondsFromNow = (i*amountOfTimeInFuture)/resolution;
                Vector3 predictedPosition = predictions.onGroundKinematicBody(opponentCar, secondsFromNow).getPosition();

                // print a small segment on the trajectory
                renderer.drawLine3d(Color.ORANGE, previousPredictedPosition, predictedPosition);

                // setup for the next prediction so we can draw the next segment
                previousPredictedPosition = predictedPosition;
            }
        }

        // try to predict the point with which we should try to hit the getNativeBallPrediction
        double timeBeforeReachingBall = predictions.timeToReachAerialDestination(input.ball.position.minus(opponentCar.position), input.ball.velocity.minus(opponentCar.velocity));
        renderer.drawRectangle3d(Color.CYAN, predictions.aerialKinematicBody(opponentCar.position, opponentCar.velocity, timeBeforeReachingBall).getPosition(), 10, 10, true);

        // draw find the same timed point, but on the getNativeBallPrediction trajectory instead
        renderer.drawRectangle3d(Color.green, predictions.getNativeBallPrediction(input.ball.position, timeBeforeReachingBall).getPosition(), 10, 10, true);
    }
}
