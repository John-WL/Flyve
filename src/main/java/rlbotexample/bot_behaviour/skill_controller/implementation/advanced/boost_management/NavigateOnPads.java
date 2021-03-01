package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.boost_management;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.GroundTrajectoryFollower;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.boost.BoostManager;
import rlbotexample.input.dynamic_data.boost.BoostPad;
import rlbotexample.input.dynamic_data.boost.BoostPadNavigation;
import rlbotexample.input.prediction.Trajectory3D;
import util.math.vector.MovingPoint;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class NavigateOnPads extends SkillController {

    private final GroundTrajectoryFollower groundTrajectoryFollower;

    private final BotBehaviour bot;

    public NavigateOnPads(BotBehaviour bot) {
        this.bot = bot;
        this.groundTrajectoryFollower = new GroundTrajectoryFollower(bot);
    }

    @Override
    public void updateOutput(DataPacket input) {
        groundTrajectoryFollower.pathToFollow = ((Trajectory3D)  time -> {
            AtomicReference<Vector3> destination = new AtomicReference<>();
            Optional<BoostPad> closestFromPlayerOpt = BoostManager.closestActivePadFrom(input.car.position);
            Optional<BoostPad> closestFromBallOpt = BoostManager.closestActivePadFrom(input.ball.position);
            closestFromPlayerOpt.ifPresent(closestPadFromPlayer -> closestFromBallOpt.ifPresent(closestPadFromBall -> {
                List<Vector3> path = BoostPadNavigation.dijkstraPathFinding(closestPadFromPlayer, closestPadFromBall, input)
                        .stream()
                        .map(boostPad -> boostPad.location)
                        .collect(Collectors.toList());
                path.add(input.ball.position);
                destination.set(path.get(0));
            }));

            return destination.get();
        });
        groundTrajectoryFollower.boostEnabled(false);
        groundTrajectoryFollower.updateOutput(input);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        MovingPoint movingPoint = groundTrajectoryFollower.pathToFollow.first(5, 1.0/60);
        if(movingPoint != null) {
            shapeRenderer.renderCross(movingPoint.physicsState.offset, Color.CYAN);
        }
        //shapeRenderer.renderTrajectory(groundTrajectoryFollower.pathToFollow, 5, Color.YELLOW);
    }
}
