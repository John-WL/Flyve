package rlbotexample.bot_behaviour.panbot.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.RlUtils;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.renderers.ShapeRenderer;
import util.math.vector.Vector3;

import java.awt.*;

public class DebugPredictedAerialHitOnBall extends FlyveBot {

    private Predictions predictions;

    public DebugPredictedAerialHitOnBall() {
        this.predictions = new Predictions();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        // print player traj (in-air, parabola, no drive prediction)
        /*
        shapeRenderer.renderParabola3D(new Parabola3D(input.allCars.get(1-input.playerIndex).position, input.allCars.get(1-input.playerIndex).velocity, new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH), 0),
                RlUtils.BALL_PREDICTION_TIME,
                Color.BLUE);
        */
        Vector3 previousCarPosition = input.allCars.get(input.playerIndex).position;
        int resolutionForCar = 100;
        for(int i = 0; i < resolutionForCar; i++) {
            double secondsInTheFuture = (RlUtils.BALL_PREDICTION_TIME*i)/resolutionForCar;
            Vector3 futureCarPosition = input.statePrediction.carsAtTime(secondsInTheFuture).get(input.playerIndex).position;
            renderer.drawLine3d(Color.YELLOW, previousCarPosition, futureCarPosition);
            previousCarPosition = futureCarPosition;
        }
        // shapeRenderer.renderHitBox(input.allCars.get(1-input.playerIndex).hitBox, Color.yellow);

        // print the predicted hit on the ball
        /*double timeOfIntersection = predictions.findIntersectionTimeBetweenAerialPlayerPositionAndCustomBallPrediction(input.allCars.get(input.playerIndex), input.ball, input.ballPrediction);
        if(timeOfIntersection < 5) {
            Orientation playerOrientation = new Orientation(input.allCars.get(input.playerIndex).orientation.noseVector, input.allCars.get(input.playerIndex).orientation.roofVector);
            HitBox playerHitBox = input.allCars.get(input.playerIndex).hitBox;
            Vector3 futurePlayerPosition = new Parabola3D(input.allCars.get(input.playerIndex).position, input.allCars.get(input.playerIndex).velocity, new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH), 0).compute(timeOfIntersection);
            Vector3 futureBallPosition = input.ballPrediction.ballAtTime(timeOfIntersection).position;

            HitBox futurePlayerHitBox = playerHitBox.generateHypotheticalHitBox(futurePlayerPosition, playerOrientation);
            Vector3 hitPoint = futurePlayerHitBox.projectPointOnSurface(futureBallPosition);

            shapeRenderer.renderCross(hitPoint, Color.CYAN);
            shapeRenderer.renderHitBox(futurePlayerHitBox, Color.LIGHT_GRAY);
        }*/

        Vector3 previousBallPosition = input.ball.position;
        int resolution = 100;
        for(int i = 0; i < resolution; i++) {
            double secondsInTheFuture = (RlUtils.BALL_PREDICTION_TIME*i)/resolution;
            Vector3 futureBallPosition = input.statePrediction.ballAtTime(secondsInTheFuture).position;
            renderer.drawLine3d(Color.red, previousBallPosition, futureBallPosition);
            previousBallPosition = futureBallPosition;
        }
    }
}
