package parameter_search.air_dribbling;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.test_controller.AirDribbleTest5;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.panbot.PanBot;
import util.machine_learning_models.evaluators.AirDribbleEvaluatorLogger2;
import util.machine_learning_models.evaluators.BotEvaluator;
import util.game_situation.*;
import util.game_situation.handlers.FiniteTrainingPack;
import util.game_situation.handlers.GameSituationHandler;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.machine_learning_models.binary_search.BinarySearchHandler;

import java.awt.*;

public class AirDribbleParameterSearcher extends PanBot {

    private SkillController skillController;
    private GameSituationHandler trainingPack;
    private BotEvaluator botEvaluator;
    private BinarySearchHandler binarySearchHandler;
    private AirDribbleParameterSearcherFileData dataRepresentation;

    public AirDribbleParameterSearcher() {
        trainingPack = new FiniteTrainingPack();
        trainingPack.add(new RemoveResidualVelocity());
        trainingPack.add(new AirDribbleSetup1());

        dataRepresentation = new AirDribbleParameterSearcherFileData();
        binarySearchHandler = new BinarySearchHandler<>(dataRepresentation);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situations handling
        trainingPack.update();

        // update bot's evaluation so we can know if the updated parameters
        // are better or worse than the current best ones we have
        botEvaluator.updateEvaluation(input);

        // if we completed the pack, then we need to modify the best parameters we have yet
        // so we can restart the training pack with fresh new parameters to test.
        if(trainingPack.hasBeenCompleted()) {
            trainingPack.reset();

            // send the result of the parameter change to the binary searchHandler,
            // so it can know if the change was a great one or a bad one,
            // and act accordingly to that matter.
            double currentEvaluation = botEvaluator.getEvaluation();
            botEvaluator.resetEvaluation();
            binarySearchHandler.sendSearchResult(currentEvaluation);

            // if we searched as much as we wanted
            if(!binarySearchHandler.isDoneSearching()) {

                // modify slightly the parameters for the next training pack sequence
                binarySearchHandler.nextHypothesis();
            }
            else {
                // isolate the best results so we can find them easily after all the file creation that happened
                dataRepresentation.isolateBestResultsInFinalDataFolder();
            }
        }

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
