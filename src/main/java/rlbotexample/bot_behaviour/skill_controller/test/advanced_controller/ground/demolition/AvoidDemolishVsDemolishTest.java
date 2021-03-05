package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.demolition;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.defense.AvoidDemolish;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.demos.Demolish;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;

public class AvoidDemolishVsDemolishTest extends FlyveBot {

    private AvoidDemolish avoidDemolishController;
    private Demolish demolishController1;
    private Demolish demolishController2;
    private TrainingPack gameSituationHandler;

    public AvoidDemolishVsDemolishTest() {
        gameSituationHandler = new CircularTrainingPack();
        avoidDemolishController = new AvoidDemolish(this);
        demolishController1 = new Demolish(this);
        demolishController2 = new Demolish(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();
        if(input.playerIndex == 0) {
            avoidDemolishController.setPlayerToAvoid(1);
            avoidDemolishController.updateOutput(input);
        }
        else if(input.playerIndex == 1) {
            demolishController1.setPlayerToDemolish(0);
            demolishController1.updateOutput(input);
        }
        else {
            demolishController2.setPlayerToDemolish(0);
            demolishController2.updateOutput(input);
        }

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        //avoidDemolishController.debug(renderer, input);
        //demolishController1.debug(renderer, input);
        //demolishController2.debug(renderer, input);
    }
}
