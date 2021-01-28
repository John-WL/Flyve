package rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AngularAccelerationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.car_orientation.AerialOrientationTesterSetup;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class AngularAccelerationControllerTest extends FlyveBot {

    private AngularAccelerationController angularAccelerationController;
    private TrainingPack gameSituationHandler;

    public AngularAccelerationControllerTest() {
        //gameSituationHandler = new CircularTrainingPack();
        //gameSituationHandler.add(new AerialOrientationTesterSetup());
        angularAccelerationController = new AngularAccelerationController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();

        angularAccelerationController.setAngularAcceleration(input.allCars.get(1-input.playerIndex).orientation.noseVector.scaled(10));
        angularAccelerationController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        angularAccelerationController.debug(renderer, input);
    }
}
