package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.aerial_directional_hit_7.states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.state_machine.State;

import java.awt.*;

public class DriveTowardAerialState implements State {

    private final BotBehaviour bot;
    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController groundOrientationController;

    private Trajectory3D approximatePlayerTrajectory;
    private Vector3 deltaVelocity;
    private Vector3 approximatePlayerPositionAtImpact;
    private Vector3 approximatePlayerVelocityAtImpact;
    private AerialTrajectoryInfo aerialInfo;

    public DriveTowardAerialState(BotBehaviour bot) {
        this.bot = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);

        this.approximatePlayerTrajectory = null;
        this.deltaVelocity = new Vector3();
        this.approximatePlayerPositionAtImpact = new Vector3();
        this.approximatePlayerVelocityAtImpact = new Vector3();
        this.aerialInfo = new AerialTrajectoryInfo();
    }

    @Override
    public void exec(DataPacket input) {
        AerialAccelerationFinder aerialAccelerationFinder = new AerialAccelerationFinder(RawBallTrajectory.trajectory);
        aerialInfo = aerialAccelerationFinder.findAerialTrajectoryInfo(0, input.car);

        approximatePlayerTrajectory = new Parabola3D(
                input.car.position,
                input.car.velocity,
                aerialInfo.acceleration.plus(RlConstants.GRAVITY_VECTOR),
                0);

        approximatePlayerPositionAtImpact = approximatePlayerTrajectory
                .compute(aerialInfo.timeOfFlight);
        approximatePlayerVelocityAtImpact = approximatePlayerTrajectory
                .derivative(aerialInfo.timeOfFlight);

        Vector3 ballDestination = StandardMapGoals.getOpponent(input.team)
                .closestPointOnSurface(approximatePlayerPositionAtImpact);

        Vector3 minimalDesiredVelocityAtImpact = ballDestination
                .minus(approximatePlayerPositionAtImpact)
                .scaledToMagnitude(1500);

        deltaVelocity = minimalDesiredVelocityAtImpact
                .minus(approximatePlayerVelocityAtImpact);
        deltaVelocity = deltaVelocity.scaled(-1);

        groundOrientationController.setDestination(deltaVelocity.scaledToMagnitude(1000).plus(input.car.position));
        groundOrientationController.updateOutput(input);

        drivingSpeedController.setSpeed(deltaVelocity.magnitude());
        drivingSpeedController.updateOutput(input);

        bot.output().boost(deltaVelocity.magnitude() > 1500);
    }

    @Override
    public State next(DataPacket input) {
        if(deltaVelocity.magnitude() < 1000
                && aerialInfo.timeOfFlight < 3) {
            return new AerialState(bot);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(approximatePlayerTrajectory, 5, Color.CYAN);
        renderer.drawString3d("for me?!", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
    }
}
