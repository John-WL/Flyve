package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.DribbleAvoidingOpponent;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.ground_dribble.GroundDribbleSetup1;
import util.game_situation.situations.ground_dribble.GroundDribbleSetup2;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class DribbleAvoidingOpponentTest extends FlyveBot {

    private DribbleAvoidingOpponent dribbleController;
    private TrainingPack gameSituationHandler;

    public DribbleAvoidingOpponentTest() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new GroundDribbleSetup1());
        gameSituationHandler.add(new GroundDribbleSetup2());
        dribbleController = new DribbleAvoidingOpponent(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();

        dribbleController.setPlayerToAvoid(1 - input.playerIndex);
        if (input.team == 1) {
            dribbleController.setBallDestination(new Vector3(0, -5200, 100));
        } else {
            dribbleController.setBallDestination(new Vector3(0, 5200, 100));
        }
        dribbleController.updateOutput(input);
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        dribbleController.debug(renderer, input);

    }
}
