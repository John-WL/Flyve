package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.defense.AvoidDemolish;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Demolish;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;

public class AvoidDemolishVsDemolishTest extends PanBot {

    private AvoidDemolish avoidDemolishController;
    private Demolish demolishController;
    private TrainingPack gameSituationHandler;

    public AvoidDemolishVsDemolishTest() {
        gameSituationHandler = new CircularTrainingPack();
        avoidDemolishController = new AvoidDemolish(this);
        demolishController = new Demolish(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();
        if(input.playerIndex == 0) {
            avoidDemolishController.setPlayerToAvoid(1);
            avoidDemolishController.updateOutput(input);
        }
        else {
            demolishController.setPlayerToDemolish(0);
            demolishController.updateOutput(input);
        }

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        avoidDemolishController.debug(renderer, input);
        demolishController.debug(renderer, input);
    }
}
