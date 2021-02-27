package rlbotexample.bot_behaviour.flyve.debug.player_ball_interaction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.prediction.gamestate_prediction.object_collisions.BallCollisionWithCar;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.ball_hit_on_car.BallHitOnCar1;
import util.game_situation.situations.ball_hit_on_car.BallHitOnCar2;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class DebugBallHitOnCar extends FlyveBot {

    private TrainingPack trainingPack;

    public DebugBallHitOnCar() {
        this.trainingPack = new CircularTrainingPack();
        this.trainingPack.add(new BallHitOnCar1());
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        if(trainingPack.updatingWontBreakBot(input)) {
            //trainingPack.update();
        }

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderHitBox(input.car.hitBox, Color.YELLOW);

        // just debug the ball and see if it's the right direction lul
        BallData previousBall = input.ball;
        int divisor = 0;
        for(BallData nextBall: input.statePrediction.balls) {
            divisor++;
            divisor %= 1;

            if(divisor == 0) {
                renderer.drawLine3d(new Color(229, 0, 229), previousBall.position.toFlatVector(), nextBall.position.toFlatVector());
                previousBall = nextBall;
            }
        }
    }
}
