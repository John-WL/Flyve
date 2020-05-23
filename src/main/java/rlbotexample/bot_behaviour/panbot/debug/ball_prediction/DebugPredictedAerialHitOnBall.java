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
import rlbotexample.input.dynamic_data.RlUtils;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.renderers.ShapeRenderer;
import util.vector.Vector3;

import java.awt.*;

public class DebugPredictedAerialHitOnBall extends PanBot {

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
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        shapeRenderer.renderParabola3D(new Parabola3D(input.car.position, input.car.velocity, new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH), 0),
                RlUtils.BALL_PREDICTION_TIME,
                Color.ORANGE);
        shapeRenderer.renderHitBox(input.car.hitBox, Color.yellow);

        double timeOfIntersection = predictions.findIntersectionTimeBetweenAerialPlayerPositionAndBall(input.car, input.ball);
        if(timeOfIntersection < 6) {
            Orientation playerOrientation = new Orientation(input.car.orientation.noseVector, input.car.orientation.roofVector);
            HitBox playerHitBox = input.car.hitBox;
            Vector3 futurePlayerPosition = new Parabola3D(input.car.position, input.car.velocity, new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH), 0).compute(timeOfIntersection);
            Vector3 futureBallPosition = input.ballPrediction.ballAtTime(timeOfIntersection).position;

            HitBox futurePlayerHitBox = playerHitBox.generateHypotheticalHitBox(futurePlayerPosition, playerOrientation);
            Vector3 hitPoint = futurePlayerHitBox.projectPointOnSurface(futureBallPosition);

            shapeRenderer.renderCross(hitPoint, Color.CYAN);
            shapeRenderer.renderHitBox(futurePlayerHitBox, Color.LIGHT_GRAY);
        }

        Vector3 previousBallPosition = input.ball.position;
        int resolution = 30;
        for(int i = 0; i < resolution; i++) {
            double secondsInTheFuture = (RlUtils.BALL_PREDICTION_TIME*i)/resolution;
            Vector3 futureBallPosition = input.ballPrediction.ballAtTime(secondsInTheFuture).position;
            renderer.drawLine3d(Color.red, previousBallPosition, futureBallPosition);
            previousBallPosition = futureBallPosition;
        }
    }
}
