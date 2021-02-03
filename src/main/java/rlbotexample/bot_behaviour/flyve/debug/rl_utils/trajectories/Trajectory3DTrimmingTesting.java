package rlbotexample.bot_behaviour.flyve.debug.rl_utils.trajectories;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class Trajectory3DTrimmingTesting extends FlyveBot {

    public Trajectory3DTrimmingTesting() {

    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        double maxBallHeight = RlConstants.BALL_RADIUS + (RlConstants.OCTANE_ROOF_ELEVATION_WHEN_DRIVING);
        Trajectory3D ballsReachableFromGround = RawBallTrajectory.trajectory
                .remove(movingPoint -> movingPoint.currentState.offset.z > maxBallHeight);
        MovingPoint firstValidBall = ballsReachableFromGround
                .first(5, 1.0/RawBallTrajectory.PREDICTION_REFRESH_RATE);

        if(firstValidBall != null) {
            shapeRenderer.renderCross(firstValidBall.currentState.offset, Color.MAGENTA);
        }
        shapeRenderer.renderTrajectory(ballsReachableFromGround, 4, Color.CYAN);
    }
}
