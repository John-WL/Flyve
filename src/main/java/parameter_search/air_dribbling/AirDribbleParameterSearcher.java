package parameter_search.air_dribbling;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.basic_skills.AirDribbleTest3;
import rlbotexample.bot_behaviour.basic_skills.SkillController;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.path.PathHandler;
import rlbotexample.bot_behaviour.path.test_paths.RandomAerialPath;
import util.game_situation.*;
import util.game_situation.handlers.FiniteTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.parameter_configuration.binary_search.BinarySearchHandler;

public class AirDribbleParameterSearcher extends PanBot {

    private CarDestination desiredDestination;
    private SkillController skillController;
    private GameSituationHandler trainingPack;
    private PathHandler pathHandler;
    private BotEvaluator botEvaluator;
    private BinarySearchHandler binarySearchHandler;

    public AirDribbleParameterSearcher() {
        trainingPack = new FiniteTrainingPack();
        trainingPack.add(new RemoveResidualVelocity());
        trainingPack.add(new AirDribbleSetup1());
        trainingPack.add(new RemoveResidualVelocity());
        trainingPack.add(new AirDribbleSetup2());
        trainingPack.add(new RemoveResidualVelocity());
        trainingPack.add(new AirDribbleSetup3());

        desiredDestination = new CarDestination();
        pathHandler = new RandomAerialPath(desiredDestination);
        skillController = new AirDribbleTest3(desiredDestination, this);
        botEvaluator = new AirDribbleEvaluator(desiredDestination);
        binarySearchHandler = new BinarySearchHandler();
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situations handling
        trainingPack.update();

        botEvaluator.updateEvaluation(input);

        if(trainingPack.hasBeenCompleted()) {
            trainingPack.reset();
            double currentEvaluation = botEvaluator.getEvaluation();
            botEvaluator.resetEvaluation();
            binarySearchHandler.sendSearchResult(currentEvaluation);

            // if the search has concluded
            if(binarySearchHandler.isDoneSearching()) {
                binarySearchHandler.isolateBestResultsInAFolder();
            }
            else {
                binarySearchHandler.nextHypothesis();
            }
        }







        // bot's desired position advances one step
        pathHandler.updateDestination(input);

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
