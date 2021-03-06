package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.weak_dribble;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.weak_dribble.WeakDribble7;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.TrainingPack;

public class WeakDribble7Test extends FlyveBot {

    private WeakDribble7 dribbleController;
    private TrainingPack gameSituationHandler;

    public WeakDribble7Test() {
        //gameSituationHandler = new CircularTrainingPack();
        //gameSituationHandler.add(new GroundDribbleSetup1());
        //gameSituationHandler.add(new GroundDribbleSetup2());
        dribbleController = new WeakDribble7(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //if(gameSituationHandler.canLoad(input)) {
            //gameSituationHandler.update();
        //}

        dribbleController.setBallImpulse(
                //((Trajectory3D) t -> new Vector3()));
                //((Trajectory3D) t -> input.allCars.get(1-input.playerIndex).position));
                input.statePrediction.ballAsTrajectory()
                        .modify(m -> StandardMapGoals.getOpponent(input.team)
                                .closestPointOfBallOnSurface(m.physicsState.offset).orElseGet(null).scaled(0.5, 1, 1)));
        double speed = 1200;

        output().boost(false);
        if(input.car.velocity.magnitude() < speed) {
            //output().boost(true);
        }

        dribbleController.setSpeed(speed);
        dribbleController.setupAndUpdateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        dribbleController.debug(renderer, input);
    }
}
