package rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.CrudeSpinController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.TrainingPack;

public class SpinControllerTest extends FlyveBot {

    private CrudeSpinController spinController;
    private TrainingPack gameSituationHandler;

    public SpinControllerTest() {
        //gameSituationHandler = new CircularTrainingPack();
        //gameSituationHandler.add(new AerialOrientationTesterSetup());
        spinController = new CrudeSpinController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();

        spinController.setSpin(input.allCars.get(1-input.playerIndex).orientation.noseVector.scaled(5.5));
        spinController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        spinController.debug(renderer, input);
    }
}
