package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.setup.ground_to_aerial;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.recovery.AerialRecovery;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.ball.BallData;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.state_machine.State;

import java.awt.*;

public class GroundState implements State {

    private final BotBehaviour bot;

    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController groundOrientationController;
    private final AerialRecovery aerialRecoveryController;

    private Vector3 desiredVelocity;
    private BallData desiredBall;
    private AerialTrajectoryInfo aerialInfo;

    public GroundState(BotBehaviour bot) {
        this.bot = bot;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.aerialRecoveryController = new AerialRecovery(bot);

        this.desiredVelocity = new Vector3();
        this.desiredBall = new BallData(new Vector3(), new Vector3(), new Vector3(), 0);
        this.aerialInfo = new AerialTrajectoryInfo();
    }

    @Override
    public void exec(DataPacket input) {
        aerialInfo = new AerialAccelerationFinder(input.statePrediction.ballAsTrajectory())
                .findAerialTrajectoryInfo(0, input.car);
        desiredBall = input.statePrediction.ballAtTime(aerialInfo.timeOfFlight);
        desiredVelocity = new Vector3(
                desiredBall.position.minus(input.car.position)
                        .toFrameOfReference(input.car.orientation)
                        .flatten(),
                0)
                .matrixRotation(input.car.orientation)
                .scaled(1/aerialInfo.timeOfFlight);
        bot.output().boost(false);
        if(desiredVelocity.magnitude() > 100
                && desiredVelocity.minus(input.car.velocity).normalized()
                        .dotProduct(input.car.orientation.noseVector) > 0
                && !input.car.isSupersonic) {
            bot.output().boost(true);
        }
        drivingSpeedController.setSpeed(desiredVelocity.magnitude());
        drivingSpeedController.updateOutput(input);
        groundOrientationController.setDestination(input.car.position.minus(desiredVelocity));
        groundOrientationController.updateOutput(input);
        aerialRecoveryController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(desiredVelocity.minus(input.car.velocity).magnitude() < 500
                && input.car.boost*RlConstants.ACCELERATION_DUE_TO_BOOST_IN_AIR /33.333333 > aerialInfo.timeOfFlight
                && desiredBall.position.z > 200) {
            return new AerialState(bot);
        }
        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("ground state", Color.YELLOW, input.car.position.toFlatVector(), 1, 1);
        renderer.drawLine3d(Color.CYAN, desiredVelocity.plus(input.car.position).toFlatVector(), input.car.position.toFlatVector());
    }
}
