package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.GroundDirectionalHit3;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class GroundDirectionalHit3Test extends FlyveBot {

    private GroundDirectionalHit3 groundDirectionalHit;
    private TrainingPack gameSituationHandler;

    public GroundDirectionalHit3Test() {
        gameSituationHandler = new CircularTrainingPack();
        groundDirectionalHit = new GroundDirectionalHit3(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //gameSituationHandler.update();

        if (input.team == 1) {
            groundDirectionalHit.setBallDestination(new Vector3(0, -5200, 100));
        } else {
            groundDirectionalHit.setBallDestination(new Vector3(0, 5200, 100));
        }
        groundDirectionalHit.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        groundDirectionalHit.debug(renderer, input);

    }
}
