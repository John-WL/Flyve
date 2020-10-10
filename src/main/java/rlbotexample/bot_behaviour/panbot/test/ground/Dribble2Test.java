package rlbotexample.bot_behaviour.panbot.test.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense.Dribble2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.*;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;

public class Dribble2Test extends PanBot {

    private Dribble2 dribbleController;
    private GameSituationHandler gameSituationHandler;

    public Dribble2Test() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new GroundDribbleSetup1());
        gameSituationHandler.add(new GroundDribbleSetup2());
        dribbleController = new Dribble2(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        gameSituationHandler.update();

        dribbleController.setBallDestination(input.allCars.get(1-input.playerIndex).position.scaled(-1, 1, 1));
        //dribbleController.setBallDestination(new Vector3(0, 0, 0));
        dribbleController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        dribbleController.debug(renderer, input);

    }
}
