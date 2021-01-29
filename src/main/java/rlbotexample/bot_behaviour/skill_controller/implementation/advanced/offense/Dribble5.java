package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

// recovers dribble if it lost the ball, and dribbles using "throttle" and "steer" for dribbles
public class Dribble5 extends SkillController {

    private final BotBehaviour bot;
    private final Dribble4 dribbleController;
    private final DrivingSpeedController drivingSpeedController;

    private double throttleAmount;
    private double steerAmount;

    public Dribble5(BotBehaviour bot) {
        this.bot = bot;
        this.dribbleController = new Dribble4(bot);
        this.drivingSpeedController = new DrivingSpeedController(bot);

        this.throttleAmount = 0;
        this.steerAmount = 0;
    }

    public void throttle(double throttleAmount) {
        this.throttleAmount = throttleAmount;
    }

    public void steer(double steerAmount) {
        this.steerAmount = steerAmount;
    }

    @Override
    public void updateOutput(DataPacket input) {
        if(botInControl(input)) {
            dribbleController.throttle(throttleAmount);
            dribbleController.steer(steerAmount);
            dribbleController.updateOutput(input);
        }
        else {
            //dribbleController.updateOutput(input);
            //dribbleController.steer(recoverySteeringAmount(input));
            //bot.output().throttle(1);
            //bot.output().boost(false);
        }
        dribbleController.throttle(throttleAmount);
        dribbleController.steer(recoverySteeringAmount(input));
        dribbleController.updateOutput(input);
        //bot.output().throttle(1);
        bot.output().boost(false);

        if(ballBouncingTooMuchToDribble(input)) {
            drivingSpeedController.setSpeed(800);
            drivingSpeedController.updateOutput(input);
        }
    }

    private double recoverySteeringAmount(DataPacket input) {
        double recoverySteeringAmount = 10;
        Vector3 ballPositionFromCar = input.ball.position.minus(input.car.position);
        double rightnessOfTheBallFromCar = input.car.orientation.rightVector
                .dotProduct(ballPositionFromCar);
        if(rightnessOfTheBallFromCar > 0) {
            return recoverySteeringAmount;
        }
        return -recoverySteeringAmount;
    }

    private boolean ballBouncingTooMuchToDribble(DataPacket input) {
        return input.ball.position.z > 200
                || Math.abs(input.ball.velocity.z) > 300;
    }

    private boolean botInControl(DataPacket input) {
        Vector3 lowestPointOfBall = input.ball.position
                .plus(Vector3.DOWN_VECTOR.scaled(RlConstants.BALL_RADIUS));
        Vector3 highestPointOnRoofOfCarHitBox = input.car.hitBox
                .closestPointOnSurface(input.car.orientation.roofVector.scaled(100));

        return lowestPointOfBall.minus(highestPointOnRoofOfCarHitBox).magnitude() < 160
                && input.car.hasWheelContact;
    }

    @Override
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        dribbleController.debug(renderer, input);
    }
}
