package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.dribble.AirDribble2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.air_dribble.AirDribbleSetup1;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

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
        airDribbleController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        airDribbleController.debug(renderer, input);

    }
}
