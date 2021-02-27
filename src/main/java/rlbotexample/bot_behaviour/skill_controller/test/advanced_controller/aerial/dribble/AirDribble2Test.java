package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.dribble.AirDribble2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.air_dribble.AirDribbleSetup1;
import util.game_situation.situations.air_dribble.AirDribbleSetup2;
import util.game_situation.situations.air_dribble.AirDribbleSetup3;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class AirDribble2Test extends FlyveBot {

    private AirDribble2 airDribbleController;
    private TrainingPack gameSituationHandler;

    public AirDribble2Test() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new AirDribbleSetup1());
        airDribbleController = new AirDribble2(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        gameSituationHandler.update();

        airDribbleController.setBallDestination(new Vector3(0, 0, 1000));
        //airDribbleController.setBallDestination(new Vector3(1000, 0, 1000));
        //airDribbleController.setBallDestination(input.allCars.get(1-input.playerIndex).position.plus(new Vector3(0, 0, 300)));
        airDribbleController.setupAndUpdateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);


        airDribbleController.debug(renderer, input);
        /*
        Vector3 hitPositionOnCarSurface = input.car.hitBox
                .closestPointOnSurface(input.ball.position);
        Vector3 carHitVector = hitPositionOnCarSurface
                .minus(input.car.position);
        Vector3 ballHitVector = hitPositionOnCarSurface
                .minus(input.ball.position);
        renderer.drawLine3d(Color.YELLOW, carHitVector.plus(ballHitVector).scaled(1, 1, 0).plus(new Vector3(0, 0, 300)), new Vector3());

        renderer.drawLine3d(Color.GREEN, input.ball.position.minus(input.car.position).scaledToMagnitude(300), new Vector3());

        renderer.drawLine3d(Color.red, Vector3.UP_VECTOR.scaledToMagnitude(300), new Vector3());

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(input.car.position, Color.magenta);
        shapeRenderer.renderCross(input.car.hitBox.closestPointOnSurface(input.car.position), Color.cyan);
        */
    }
}
