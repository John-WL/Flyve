package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder2;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.controllers.BoostController;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class AerialController extends SkillController {

    private BotBehaviour bot;

    private final AerialOrientationController5 aerialOrientationHandler;
    private final BoostController boostController;

    private Function<Double, Vector3> targetTrajectory;
    private double maxDeltaV;

    private Vector3 orientation;

    public AerialController(BotBehaviour bot) {
        this.bot = bot;

        this.aerialOrientationHandler = new AerialOrientationController5(bot);
        this.boostController = new BoostController();

        this.maxDeltaV = Double.MAX_VALUE;
    }

    public void setTargetTrajectory(Function<Double, Vector3> targetTrajectory) {
        this.targetTrajectory = targetTrajectory;
    }

    public void setMaxDeltaV(double maxDeltaV) {
        this.maxDeltaV = maxDeltaV;
    }

    @Override
    public void updateOutput(DataPacket input) {
        AerialTrajectoryInfo trajectoryInfo = findAerialTrajectoryInfo(input);
        orientation = trajectoryInfo.acceleration;

        aerialOrientationHandler.setNoseOrientation(orientation);
        aerialOrientationHandler.setRollOrientation(new Vector3(0, 0, 10000));
        aerialOrientationHandler.updateOutput(input);

        // boost
        bot.output().boost(isNoseOriented(orientation, input)
                && boostController.process(orientation.magnitude()));
    }

    private boolean isNoseOriented(Vector3 orientation, DataPacket input) {
        return input.car.orientation.noseVector
                .dotProduct(orientation.normalized()) > 0.9;
    }

    private AerialTrajectoryInfo findAerialTrajectoryInfo(DataPacket input) {
        AtomicReference<AerialTrajectoryInfo> aerialTrajectoryInfoRef = new AtomicReference<>(new AerialTrajectoryInfo());

        Optional<AerialTrajectoryInfo> aerialTrajectoryInfoOpt = AerialAccelerationFinder2
                .findAerialTrajectoryInfoOrElse(targetTrajectory, input.car, maxDeltaV);
        aerialTrajectoryInfoOpt.ifPresent(aerialTrajectoryInfoRef::set);

        return aerialTrajectoryInfoRef.get();
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        aerialOrientationHandler.debug(renderer, input);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(targetTrajectory, 3, Color.blue);
        shapeRenderer.renderTrajectory(new Parabola3D(
                input.car.position,
                input.car.velocity,
                orientation.plus(RlConstants.GRAVITY_VECTOR),
                0), 3, Color.CYAN);
    }
}
