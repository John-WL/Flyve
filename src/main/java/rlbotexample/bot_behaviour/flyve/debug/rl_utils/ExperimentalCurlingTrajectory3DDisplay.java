package rlbotexample.bot_behaviour.flyve.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.car.Orientation;
import rlbotexample.input.prediction.ExperimentalCurlingTrajectory3D;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class ExperimentalCurlingTrajectory3DDisplay extends FlyveBot {

    private ExperimentalCurlingTrajectory3D trajectory3D;

    public ExperimentalCurlingTrajectory3DDisplay() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        ExtendedCarData carData = input.allCars.get(1-input.playerIndex);
        trajectory3D = new ExperimentalCurlingTrajectory3D(carData.position, carData.velocity, carData.orientation.noseVector.scaled(RlConstants.ACCELERATION_DUE_TO_BOOST), carData.spin);
        trajectory3D = new ExperimentalCurlingTrajectory3D(carData);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        shapeRenderer.renderTrajectory(trajectory3D, 6, Color.CYAN);
    }
}
