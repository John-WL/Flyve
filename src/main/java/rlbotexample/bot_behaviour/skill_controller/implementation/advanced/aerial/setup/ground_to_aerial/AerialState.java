package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.setup.ground_to_aerial;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.directionnal_hit.parabola.AerialDirectionalHit6;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class AerialState implements State {

    private final BotBehaviour bot;
    private final AerialDirectionalHit6 aerialDirectionalHit5;

    private Vector3 desiredVelocity;

    public AerialState(BotBehaviour bot) {
        this.bot = bot;
        this.aerialDirectionalHit5 = new AerialDirectionalHit6(bot);

        this.desiredVelocity = new Vector3();
    }

    @Override
    public void exec(DataPacket input) {
        double timeOfAerial = new AerialAccelerationFinder(input.statePrediction.ballAsTrajectory())
                .findAerialTrajectoryInfo(0, input.car).timeOfFlight;
        BallData desiredBall = input.statePrediction.ballAtTime(timeOfAerial);
        desiredVelocity = desiredBall.position.minus(input.car.position)
                .scaled(1/timeOfAerial);

        AtomicReference<Vector3> destinationRef = new AtomicReference<>(new Vector3());
        StandardMapGoals.getOpponent(input.team)
                .closestPointOfBallOnSurface(new Vector3(0, 0, 200))
        .ifPresent(destinationRef::set);
        aerialDirectionalHit5.setBallDestination(destinationRef.get());
        aerialDirectionalHit5.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(desiredVelocity.normalized().dotProduct(input.car.velocity.normalized()) < 0.6) {
            return new GroundState(bot);
        }
        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("air state", Color.YELLOW, input.car.position.toFlatVector(), 1, 1);
        renderer.drawLine3d(Color.red, desiredVelocity.plus(input.car.position).toFlatVector(), input.car.position.toFlatVector());
    }
}
