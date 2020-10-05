package rlbotexample.bot_behaviour.panbot.test.aerial;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.test_controller.AirDribbleTest3;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.path.PathHandler;
import rlbotexample.bot_behaviour.path.test_paths.RandomAerialPath;
import util.game_situation.*;
import util.game_situation.handlers.CircularTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class AirDribbleTestAndTweak extends PanBot {

    private CarDestination desiredDestination;
    private SkillController skillController;
    private GameSituationHandler gameSituationHandler;
    private PathHandler pathHandler;

    public AirDribbleTestAndTweak() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AirDribbleSetup1());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AirDribbleSetup2());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AirDribbleSetup3());

        desiredDestination = new CarDestination();
        pathHandler = new RandomAerialPath(desiredDestination);
        skillController = new AirDribbleTest3(desiredDestination, this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situations handling!
        // simple ground shots, aerial setups, defense setups, etc...
        // It helps to setup the game state so we can tweak specific bot behaviours
        // before implementing the meta game strats.
        gameSituationHandler.update();

        // bot's desired position advances one step
        pathHandler.updateDestination(input);

        /*
        if(2*desiredDestination.getThrottleDestination().z + input.getNativeBallPrediction.velocity.z < 500 ||
            input.car.position.minus(input.getNativeBallPrediction.position).flatten().magnitude() > input.getNativeBallPrediction.position.z) {
            skillController = driveToDestination;
        }
        else {
            skillController = flyToDestination;
        }
        */

        // do the thing
        skillController.setupAndUpdateOutputs(input);

        // return the calculated bot output
        return super.output();
    }



    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        skillController.debug(renderer, input);
    }
}
