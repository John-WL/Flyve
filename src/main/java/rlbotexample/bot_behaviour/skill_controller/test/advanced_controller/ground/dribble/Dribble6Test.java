package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.dribble;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.Dribble6;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class Dribble6Test extends FlyveBot {

    private Dribble6 dribbleController;
    private TrainingPack gameSituationHandler;

    private Vector3 validGoalPosition;

    public Dribble6Test() {
        //gameSituationHandler = new CircularTrainingPack();
        //gameSituationHandler.add(new GroundDribbleSetup1());
        //gameSituationHandler.add(new GroundDribbleSetup2());
        dribbleController = new Dribble6(this);

        validGoalPosition = new Vector3();
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //if(gameSituationHandler.canLoad(input)) {
            //gameSituationHandler.update();
        //}

        StandardMapGoals.getOpponent(input.team).closestPointOfBallOnSurface(input.ball.position)
        .ifPresent(v -> validGoalPosition = v);
        validGoalPosition = validGoalPosition.scaled(0.5, 1, 1);
        dribbleController.setBallDestination(validGoalPosition);
        dribbleController.setTargetSpeed(800);
        dribbleController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        dribbleController.debug(renderer, input);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(validGoalPosition, Color.MAGENTA);
    }
}
