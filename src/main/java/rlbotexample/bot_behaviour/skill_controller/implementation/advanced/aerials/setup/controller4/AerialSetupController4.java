package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.setup.controller4;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.metagame.advanced_gamestate_info.AerialInfo;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.AerialDirectionalHit5;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.AerialDirectionalHit6;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.aerials.AerialUtils;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.state_machine.State;
import util.state_machine.StateMachine;
import util.state_machine.StateMachineBuilder;

import java.awt.*;

public class AerialSetupController4 extends SkillController {

    private BotBehaviour bot;
    private AerialDirectionalHit6 aerialDirectionalHit5;
    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    private Vector3 ballDestination;

    private Parabola3D playerTrajectory;

    private StateMachine<DataPacket, Void> aerialStateMachine;

    public AerialSetupController4(BotBehaviour bot) {
        this.bot = bot;
        this.aerialDirectionalHit5 = new AerialDirectionalHit6(bot);
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);

        this.ballDestination = new Vector3();

        this.playerTrajectory = null;

        buildAerialMachine();
    }

    private void buildAerialMachine() {
        StateMachineBuilder<DataPacket, Void> stateMachineBuilder = new StateMachineBuilder<>();

        State<DataPacket, Void> setupAerial = (DataPacket input) -> {
            bot.output().boost(false);
            return null;
        };
        State<DataPacket, Void> aerial = (DataPacket input) -> {
            aerialDirectionalHit5.updateOutput(input);
            return null;
        };

        // conditions to begin the aerial
        stateMachineBuilder.addState(setupAerial, (input) -> {
            AerialTrajectoryInfo aerialInfo = AerialUtils.findAerialTrajectoryInfo(input.car, new Trajectory3D() {
                @Override
                public Vector3 compute(double time) {
                    Vector3 centerPosition = input.statePrediction.ballAtInterpolatedTime(time).position;
                    Vector3 offsetPosition = centerPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS);
                    return centerPosition.plus(offsetPosition);
                }
            });
            playerTrajectory = new Parabola3D(
                    input.car.position,
                    input.car.velocity,
                    aerialInfo.acceleration.plus(Vector3.DOWN_VECTOR.scaled(RlConstants.NORMAL_GRAVITY_STRENGTH)),
                    0
            );

            Vector3 playerDestination = input.statePrediction.ballAtTime(aerialInfo.timeOfFlight).position;
            Vector3 offsetDirection = playerDestination.minus(ballDestination).normalized();
            Vector3 playerArrivalDirection = playerTrajectory.derivative(aerialInfo.timeOfFlight).normalized();

            /*if(offsetDirection.dotProduct(playerArrivalDirection) > -0.5) {
                return setupAerial;
            }*/
            if(aerialInfo.timeOfFlight < 3) {
                return aerial;
            }

            return aerial;
        });

        // conditions to stop the aerial
        stateMachineBuilder.addState(aerial, (input) -> {
            AerialTrajectoryInfo aerialInfo = AerialUtils.findAerialTrajectoryInfo(input.car, new Trajectory3D() {
                @Override
                public Vector3 compute(double time) {
                    return input.statePrediction.ballAtTime(time).position;
                }
            });
            if(aerialInfo.timeOfFlight > 4) {
                return aerial;
            }

            return aerial;
        });

        aerialStateMachine = stateMachineBuilder.build();
    }

    public void setBallDestination(final Vector3 ballDestination) {
        this.ballDestination = ballDestination;
        this.aerialDirectionalHit5.setBallDestination(ballDestination);
    }

    @Override
    public void updateOutput(DataPacket input) {
        aerialStateMachine.update(input);
    }

    @Override
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer s = new ShapeRenderer(renderer);
        s.renderCross(ballDestination, Color.CYAN);

        s.renderParabola3D(playerTrajectory, 4, Color.CYAN);
    }
}
