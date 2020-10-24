package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Demolish;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.GroundDirectionalHit;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class DemolishTest extends PanBot {

    private Demolish demolishController;
    private TrainingPack gameSituationHandler;

    public DemolishTest() {
        gameSituationHandler = new CircularTrainingPack();
        demolishController = new Demolish(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();
        demolishController.setPlayerToDemolish(1-input.playerIndex);
        demolishController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        demolishController.debug(renderer, input);
    }
}
