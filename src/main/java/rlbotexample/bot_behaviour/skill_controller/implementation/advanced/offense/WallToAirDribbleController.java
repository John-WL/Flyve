package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

public class WallToAirDribbleController extends SkillController {

    private final GroundTrajectoryFollower groundTrajectoryFollower;

    private final BotBehaviour bot;

    public WallToAirDribbleController(BotBehaviour bot) {
        this.bot = bot;
        this.groundTrajectoryFollower = new GroundTrajectoryFollower(bot);
    }

    @Override
    public void updateOutput(DataPacket input) {
        groundTrajectoryFollower.pathToFollow = RawBallTrajectory.trajectory
                .modify(movingPoint -> movingPoint.currentState.offset.plus(new Vector3(0, 0, 0)));
        groundTrajectoryFollower.pathToFollow = groundTrajectoryFollower.pathToFollow
                .keep(movingPoint -> {
                    double distance = movingPoint.currentState.offset.distance(input.car.position);
                    double speed = distance/movingPoint.time;

                    return movingPoint.currentState.direction.magnitude()*1.8 < speed
                            && movingPoint.currentState.direction.magnitudeSquared() > -1;
                });
        groundTrajectoryFollower.boostEnabled(false);
        groundTrajectoryFollower.updateOutput(input);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        groundTrajectoryFollower.debug(renderer, input);
    }
}
