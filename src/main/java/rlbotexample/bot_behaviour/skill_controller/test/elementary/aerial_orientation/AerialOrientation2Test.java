package rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.car_orientation.AerialOrientationTesterSetup;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class AerialOrientation2Test extends FlyveBot {

    private AerialOrientationController2 aerialOrientationController2;
    private TrainingPack gameSituationHandler;

    public AerialOrientation2Test() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new AerialOrientationTesterSetup());
        aerialOrientationController2 = new AerialOrientationController2(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        gameSituationHandler.update();

        if(System.currentTimeMillis() % 4000 < 2000) {
            aerialOrientationController2.setOrientationDestination(new Vector3(0, 1000, 0));
            aerialOrientationController2.setRollOrientation(new Vector3(0, 1000, 0));
        }
        else if(System.currentTimeMillis() % 4000 < 3999) {
            aerialOrientationController2.setOrientationDestination(new Vector3(0, -1000, 0));
            aerialOrientationController2.setRollOrientation(new Vector3(0, -1000, 0));
        }
        aerialOrientationController2.setOrientationDestination(input.allCars.get(1-input.playerIndex).position);
        aerialOrientationController2.setRollOrientation(input.allCars.get(1-input.playerIndex).position);
        aerialOrientationController2.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        aerialOrientationController2.debug(renderer, input);
    }
}
