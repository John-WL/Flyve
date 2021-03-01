package rlbotexample.bot_behaviour.flyve.debug.rl_utils.trajectories;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class Trajectory3DModifyingTesting extends FlyveBot {

    public Trajectory3DModifyingTesting() {

    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        Trajectory3D ballAndItsOrientation = RawBallTrajectory.trajectory
                .modify(movingPoint -> movingPoint.physicsState.offset
                        .plus(new Vector3(0, 0, 300).rotate(RawBallTrajectory.ballAtTime(movingPoint.time).spin.scaled(movingPoint.time))));

        shapeRenderer.renderTrajectory(ballAndItsOrientation, 4, Color.CYAN, Color.MAGENTA);
    }
}
