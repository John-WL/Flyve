package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.aerial_directional_hit_7.states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit6;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;

public class AerialState implements State {

    private final BotBehaviour bot;
    private final AerialDirectionalHit6 aerialDirectionalHit;

    private Trajectory3D approximatePlayerTrajectory;
    private Vector3 deltaVelocity;

    public AerialState(BotBehaviour bot) {
        this.bot = bot;
        this.aerialDirectionalHit = new AerialDirectionalHit6(bot);

        this.approximatePlayerTrajectory = null;
        this.deltaVelocity = new Vector3();
    }

    @Override
    public void exec(DataPacket input) {
        AerialAccelerationFinder aerialAccelerationFinder = new AerialAccelerationFinder(RawBallTrajectory.trajectory);
        AerialTrajectoryInfo aerialInfo = aerialAccelerationFinder.findAerialTrajectoryInfo(0, input.car);

        approximatePlayerTrajectory = new Parabola3D(
                input.car.position,
                input.car.velocity,
                aerialInfo.acceleration.plus(RlConstants.GRAVITY_VECTOR),
                0);

        Vector3 approximatePlayerPositionAtImpact = approximatePlayerTrajectory
                .compute(aerialInfo.timeOfFlight);
        Vector3 approximatePlayerVelocityAtImpact = approximatePlayerTrajectory
                .derivative(aerialInfo.timeOfFlight);

        Vector3 ballDestination = StandardMapGoals.getOpponent(input.team)
                .closestPointOnSurface(approximatePlayerPositionAtImpact);

        Vector3 minimalDesiredVelocityAtImpact = ballDestination
                .minus(approximatePlayerPositionAtImpact)
                .scaledToMagnitude(1500);

        deltaVelocity = minimalDesiredVelocityAtImpact
                .minus(approximatePlayerVelocityAtImpact);
        deltaVelocity = deltaVelocity.scaled(-1);

        aerialDirectionalHit.setBallDestination(StandardMapGoals.getOpponent(input.team).normal.offset);
        aerialDirectionalHit.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(deltaVelocity.magnitude() > 3000) {
            return new DriveTowardAerialState(bot);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        aerialDirectionalHit.debug(renderer, input);
        renderer.drawString3d("woohoooo!", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
    }
}
