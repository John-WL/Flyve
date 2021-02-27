package rlbotexample.bot_behaviour.flyve.debug.player_ball_interaction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.object_collisions.BallCollisionWithCar;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.ball_hit_on_car.BallHitOnCar1;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class DebugBallHitNormalOnPlayer extends FlyveBot {

    private TrainingPack trainingPack;

    public DebugBallHitNormalOnPlayer() {
        this.trainingPack = new CircularTrainingPack();
        this.trainingPack.add(new BallHitOnCar1());
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        if(trainingPack.updatingWontBreakBot(input)) {
            trainingPack.update();
        }

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderHitBox(input.car.hitBox, Color.YELLOW);

        // rendering components of normal maybe
        Vector3 ballPosition = input.ball.position;
        Vector3 closestPointOfBallOnCar = input.car.hitBox.closestPointOnSurface(ballPosition);
        renderer.drawLine3d(Color.CYAN, input.car.position.toFlatVector(), ballPosition.toFlatVector());
        renderer.drawLine3d(Color.YELLOW, ballPosition.toFlatVector(), closestPointOfBallOnCar.toFlatVector());

        // rendering normal maybe
        Vector3 f = input.car.orientation.noseVector;
        Vector3 weirdN = input.ball.position.minus(input.car.position);
        weirdN = weirdN.scaled(1, 1, 0.35);
        weirdN = (weirdN.minus(f.scaled(0.35*weirdN.dotProduct(f)))).normalized();
        Vector3 hitNormalMaybe = ballPosition.minus(closestPointOfBallOnCar)
                .plus(weirdN).scaledToMagnitude(400);
        renderer.drawLine3d(Color.red, input.car.position.toFlatVector(), input.car.position.plus(hitNormalMaybe).toFlatVector());

        // rendering new speed after collision maybe
        Vector3 newSpeedParallelMaybe = input.ball.velocity.projectOnto(hitNormalMaybe).scaled(-0.5);
        Vector3 newSpeedPerpendicularMaybe = input.ball.velocity.minus(input.ball.velocity.projectOnto(hitNormalMaybe));
        Vector3 newSpeedMaybe = newSpeedParallelMaybe.plus(newSpeedPerpendicularMaybe);
        renderer.drawLine3d(Color.MAGENTA, input.ball.position.toFlatVector(), input.ball.position.plus(newSpeedMaybe.scaledToMagnitude(400)).toFlatVector());

        // rendering current ball speed maybe ("maybe" is a joke it's the actual linear speed)
        renderer.drawLine3d(Color.CYAN, input.ball.position.toFlatVector(), input.ball.position.plus(input.ball.velocity.scaledToMagnitude(400)).toFlatVector());

        System.out.println(input.ball.velocity.magnitude());
    }
}
