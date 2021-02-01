package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.GroundTrajectoryFollower;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GroundTrajectoryFollowerTest extends FlyveBot {

    private GroundTrajectoryFollower trajectoryFollower;

    public GroundTrajectoryFollowerTest() {
        trajectoryFollower = new GroundTrajectoryFollower(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        trajectoryFollower.setTrajectory(RawBallTrajectory.trajectory
                .modify(movingPoint -> movingPoint.currentState.offset
                        .plus(Vector3.DOWN_VECTOR.scaled(RlConstants.BALL_RADIUS))
                        .plus(new Vector3(70, 0, 0)))
        .keep(movingPoint -> movingPoint.currentState.offset.z < 10));
        trajectoryFollower.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        MovingPoint movingPoint = trajectoryFollower.pathToFollow.firstValid(5, 1.0/60);
        if(movingPoint != null) {
            shapeRenderer.renderCross(movingPoint.currentState.offset, Color.CYAN);
        }
    }
}
