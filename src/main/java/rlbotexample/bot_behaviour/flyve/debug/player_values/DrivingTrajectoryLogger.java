package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.trajectories.DrivingTrajectory;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class DrivingTrajectoryLogger extends FlyveBot {

    public DrivingTrajectory drivingTrajectory;

    public DrivingTrajectoryLogger() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return output();
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        //System.out.println(drivingTrajectory.totalTime);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        drivingTrajectory = new DrivingTrajectory(new Ray3(new Vector3(0, 0, 100), new Vector3(0, 1, 0)), input.allCars.get(1-input.playerIndex), 2000, shapeRenderer);
        shapeRenderer.renderTrajectory(t -> drivingTrajectory.apply(t).offset, 2, Color.red);
    }
}
