package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class CushionBouncyBall extends SkillController {

    private final GroundTrajectoryFollower groundTrajectoryFollower;

    private final BotBehaviour bot;

    public CushionBouncyBall(BotBehaviour bot) {
        this.bot = bot;
        this.groundTrajectoryFollower = new GroundTrajectoryFollower(bot);
    }

    @Override
    public void updateOutput(DataPacket input) {
        groundTrajectoryFollower.pathToFollow = RawBallTrajectory.trajectory
                .modify(movingPoint -> {
                    Vector3 offset = new Vector3(input.car.position.minus(movingPoint.currentState.offset).flatten(), 0)
                            .scaledToMagnitude(RlConstants.BALL_RADIUS*1.4);
                    return movingPoint.currentState.offset.plus(offset);
                });
        groundTrajectoryFollower.pathToFollow = groundTrajectoryFollower.pathToFollow
                .keep(movingPoint -> movingPoint.currentState.offset.z < 150
                        && movingPoint.currentState.direction.z < -300);
        groundTrajectoryFollower.boostEnabled(false);
        groundTrajectoryFollower.updateOutput(input);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        MovingPoint movingPoint = groundTrajectoryFollower.pathToFollow.firstValid(5, 1.0/60);
        if(movingPoint != null) {
            shapeRenderer.renderCross(movingPoint.currentState.offset, Color.CYAN);
        }
        shapeRenderer.renderTrajectory(groundTrajectoryFollower.pathToFollow, 5, Color.YELLOW);
    }
}