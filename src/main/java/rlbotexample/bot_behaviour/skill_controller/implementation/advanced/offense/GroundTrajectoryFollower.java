package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.MaxAccelerationFromThrottleFinder;
import rlbotexample.input.prediction.Trajectory3D;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;

public class GroundTrajectoryFollower extends SkillController {

    public static final double AMOUNT_OF_TIME_TO_SEARCH = 5;
    public static final double PRECISION = 1.0/60;

    public Trajectory3D pathToFollow;

    private final BotBehaviour bot;
    private boolean boostEnabled;

    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController groundOrientationController;

    public GroundTrajectoryFollower(BotBehaviour bot) {
        this.bot = bot;
        this.pathToFollow = null;
        this.boostEnabled = true;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
    }

    public void setTrajectory(Trajectory3D pathToFollow) {
        this.pathToFollow = pathToFollow;
    }

    public void boostEnabled(boolean boostEnabled) {
        this.boostEnabled = boostEnabled;
    }

    @Override
    public void updateOutput(DataPacket input) {
        if(pathToFollow == null) {
            return;
        }
        MovingPoint destination = pathToFollow
                .remove(movingPoint -> {
                    double distance = movingPoint.physicsState.offset
                            .distance(input.car.position);
                    double time = movingPoint.time;
                    double speedNeeded = distance/time;

                    if(!boostEnabled) {
                        double speed = input.car.velocity.magnitude();
                        if(speed > 1410) {
                            return speedNeeded > speed;
                        }
                        else {
                            return speedNeeded > 1410;
                        }
                    }
                    else {
                        return speedNeeded > RlConstants.CAR_MAX_SPEED;
                    }
                })
                .first(AMOUNT_OF_TIME_TO_SEARCH, PRECISION);
        if(destination == null) {
            drivingSpeedController.setSpeed(0);
            drivingSpeedController.updateOutput(input);

            groundOrientationController.setDestination(input.car.position
                    .plus(input.car.orientation.noseVector));
            groundOrientationController.updateOutput(input);
            bot.output().boost(false);
            bot.output().drift(false);
            bot.output().jump(false);
            bot.output().pitch(0);
            bot.output().roll(0);
            bot.output().yaw(0);

            return;
        }

        double distance = destination.physicsState.offset
                .distance(input.car.position);
        double desiredSpeed = distance/destination.time;
        double accelerationToReach = (desiredSpeed - input.car.velocity.magnitude())/destination.time;
        boolean isBoosting = accelerationToReach > MaxAccelerationFromThrottleFinder.compute(input.car.velocity.magnitude())
                && (!input.car.isSupersonic)
                && boostEnabled;
        bot.output().boost(isBoosting);

        //System.out.println(desiredSpeed);

        drivingSpeedController.setSpeed(desiredSpeed);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(destination.physicsState.offset);
        groundOrientationController.updateOutput(input);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {

    }
}
