package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.AerialController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.game_situation.miscellaneous.RemoveResidualVelocity;
import util.game_situation.situations.aerial_hit.AerialHitSetup1;
import util.game_situation.situations.aerial_hit.AerialHitSetup2;
import util.game_situation.situations.aerial_hit.AerialHitSetup3;
import util.game_situation.situations.aerial_hit.AerialHitSetup4;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

import java.util.concurrent.atomic.AtomicReference;

public class AerialControllerTest extends FlyveBot {

    private AerialController aerialController;
    private JumpController jumpController;
    private TrainingPack gameSituationHandler;

    public AerialControllerTest() {
        aerialController = new AerialController(this);
        jumpController = new JumpController(this);

        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup1());
        gameSituationHandler.add(new AerialHitSetup1());
        gameSituationHandler.add(new AerialHitSetup2());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup3());
        gameSituationHandler.add(new RemoveResidualVelocity());
        gameSituationHandler.add(new AerialHitSetup4());
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // game situation handling
        if(gameSituationHandler.updatingWontBreakBot(input)) {
            //gameSituationHandler.update();
        }

        aerialController.setTargetTrajectory(RawBallTrajectory.trajectory
                .andThen(ballPosition -> {
                    AtomicReference<Vector3> ballOffset = new AtomicReference<>(new Vector3());
                    StandardMapGoals.getOpponent(input.team)
                                    .closestPointOfBallOnSurface(ballPosition)
                            .ifPresent(v -> ballOffset.set(ballPosition.minus(v).scaledToMagnitude(RlConstants.BALL_RADIUS)));
                    return ballPosition.plus(ballOffset.get());
                }));
        aerialController.setMaxDeltaV(2000);
        aerialController.updateOutput(input);

        jumpController.setFirstJumpType(new SimpleJump(), input);
        jumpController.setSecondJumpType(new ShortJump(), input);
        jumpController.updateOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        aerialController.debug(renderer, input);
    }
}
