package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.defense.AvoidDemolish;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;

public class AvoidDemolishTest extends FlyveBot {

    private AvoidDemolish demolishController;
    private TrainingPack gameSituationHandler;

    public AvoidDemolishTest() {
        gameSituationHandler = new CircularTrainingPack();
        demolishController = new AvoidDemolish(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();
        demolishController.setPlayerToAvoid(1-input.playerIndex);
        demolishController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        demolishController.debug(renderer, input);
    }
}
