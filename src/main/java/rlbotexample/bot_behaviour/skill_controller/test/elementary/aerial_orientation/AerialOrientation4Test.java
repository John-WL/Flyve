package rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController4;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.game_situation.situations.car_orientation.AerialOrientationTesterSetup;
import util.game_situation.trainning_pack.CircularTrainingPack;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Vector3;

public class AerialOrientation4Test extends FlyveBot {

    private AerialOrientationController4 aerialOrientationController4;
    private TrainingPack gameSituationHandler;

    private Vector3 noseOrientation = new Vector3();
    private Vector3 roofOrientation = new Vector3();

    private int frameCounter = 8*(int)RlConstants.BOT_REFRESH_RATE;

    public AerialOrientation4Test() {
        gameSituationHandler = new CircularTrainingPack();
        gameSituationHandler.add(new AerialOrientationTesterSetup());
        aerialOrientationController4 = new AerialOrientationController4(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        if(gameSituationHandler.updatingWontBreakBot(input)) {
            gameSituationHandler.update();
        }

        frameCounter++;
        if(frameCounter*RlConstants.BOT_REFRESH_TIME_PERIOD > 8) {
            frameCounter = 0;
            noseOrientation = new Vector3(Math.random()*2-1, Math.random()*2-1, Math.random()*2-1);
            roofOrientation = new Vector3(Math.random()*2-1, Math.random()*2-1, Math.random()*2-1);
        }
        //aerialOrientationController4.setNoseOrientation(noseOrientation);
        //aerialOrientationController4.setRollOrientation(roofOrientation);
        aerialOrientationController4.setNoseOrientation(input.allCars.get(1-input.playerIndex).orientation.noseVector);
        aerialOrientationController4.setRollOrientation(input.allCars.get(1-input.playerIndex).orientation.roofVector);
        aerialOrientationController4.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        aerialOrientationController4.debug(renderer, input);
    }
}
