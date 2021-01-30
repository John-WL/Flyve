package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Dribble2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Dribble4;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_situation.situations.ground_dribble.GroundDribbleSetup1;
import util.game_situation.situations.ground_dribble.GroundDribbleSetup2;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class Dribble4Test extends FlyveBot {

    private Dribble4 dribbleController;
    private TrainingPack gameSituationHandler;

    public Dribble4Test() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new GroundDribbleSetup1());
        gameSituationHandler.add(new GroundDribbleSetup2());
        dribbleController = new Dribble4(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        if(gameSituationHandler.canLoad(input)) {
            //gameSituationHandler.update();
        }

        dribbleController.throttle(1);
        if(input.ball.velocity.magnitude() > 400) {
            dribbleController.throttle(0);
        }
        dribbleController.steer(0);
        if(input.ball.position.minus(input.car.position).magnitude() > 200) {
            dribbleController.steer(1.12);
        }
        dribbleController.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        dribbleController.debug(renderer, input);

    }
}
