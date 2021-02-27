package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.controllers.PidController;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class Dribble4 extends SkillController {

    private final BotBehaviour bot;
    private Vector3 neutralBallDestination;
    private Vector3 desiredBallPositionOnPlayerCar;
    private Vector3 smoothedOutDesiredBallPositionOnPlayerCar;

    private double throttleAmount;
    private double steerAmount;

    private final PidController steerPid;

    private DrivingSpeedController drivingSpeedController;

    public Dribble4(BotBehaviour bot) {
        this.bot = bot;
        this.neutralBallDestination = new Vector3();
        this.desiredBallPositionOnPlayerCar = new Vector3();
        this.smoothedOutDesiredBallPositionOnPlayerCar = new Vector3();

        this.throttleAmount = 0;
        this.steerAmount = 0;

        this.steerPid = new PidController(5, 0, 1);

        this.drivingSpeedController = new DrivingSpeedController(bot);
    }

    public void throttle(double throttleAmount) {
        this.throttleAmount = throttleAmount;
    }

    public void steer(double steerAmount) {
        this.steerAmount = steerAmount;
    }

    @Override
    public void updateOutput(DataPacket input) {
        desiredBallPositionOnPlayerCar = findOptimalBallPositionOnPlayerCar(input);
        double smoothAmount = 1;
        smoothedOutDesiredBallPositionOnPlayerCar = smoothedOutDesiredBallPositionOnPlayerCar.scaled(1-smoothAmount)
                .plus(desiredBallPositionOnPlayerCar.minus(input.car.position)
                        .scaled(smoothAmount));

        final Vector3 playerDeltaDestination = input.ball.position.minus(smoothedOutDesiredBallPositionOnPlayerCar.plus(input.car.position));

        final double rawSteeringAmount = playerDeltaDestination
                .plus(input.car.orientation.noseVector.scaled(200))
                .flatten()
                .correctionAngle(input.car.orientation.noseVector.flatten());
        double steerAmount = steerPid.process(rawSteeringAmount, 0);
        bot.output().steer(steerAmount);
        //bot.output().drift(steerAmount > 5);


        double rawThrottlingAmount = playerDeltaDestination.dotProduct(input.car.orientation.noseVector)*40;
        drivingSpeedController.setSpeed(input.ball.velocity.flatten().magnitude() + rawThrottlingAmount);
        drivingSpeedController.updateOutput(input);
        bot.output().boost(rawThrottlingAmount > 1200);

        if(input.car.position.minus(input.ball.position).magnitude() > 300) {
            bot.output().boost(false);
        }
    }

    private Vector3 findOptimalBallPositionOnPlayerCar(DataPacket input) {
        neutralBallDestination = input.car.position.plus(new Vector3(0, 0, 50));
        Vector3 optimalLeftAndRightOffset = findLeftAndRightBallOffsetFromCar(input);
        Vector3 optimalFrontAndBackOffset = findFrontAndBackBallOffsetFromCar(input);
        return neutralBallDestination.plus(optimalLeftAndRightOffset).plus(optimalFrontAndBackOffset);
    }

    private Vector3 findFrontAndBackBallOffsetFromCar(DataPacket input) {
        // front is positive, back negative
        double centerOffsetDistanceY = input.car.hitBox.centerPositionOfHitBox.minus(input.car.position)
                .flatten()
                .magnitude();
        double allowedRangeOfBallPositionOnCar = input.car.hitBox.cornerPosition.x + RlConstants.BALL_RADIUS/5;
        double optimalFrontAndBackOffsetAmount;

        if(throttleAmount > 0) {
            optimalFrontAndBackOffsetAmount = throttleAmount * (allowedRangeOfBallPositionOnCar + centerOffsetDistanceY);
        }
        else {
            optimalFrontAndBackOffsetAmount = throttleAmount * (allowedRangeOfBallPositionOnCar - centerOffsetDistanceY);
        }

        return input.car.orientation.noseVector.scaled(optimalFrontAndBackOffsetAmount);
    }

    private Vector3 findLeftAndRightBallOffsetFromCar(DataPacket input) {
        // right is positive, and left is negative. Just a convention
        double allowedRangeOfBallPositionOnCar = input.car.hitBox.cornerPosition.y + RlConstants.BALL_RADIUS/1.55;
        double optimalLeftAndRightOffsetAmount = steerAmount * allowedRangeOfBallPositionOnCar;
        return input.car.orientation.rightVector.scaled(optimalLeftAndRightOffsetAmount);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.red, neutralBallDestination.toFlatVector(), desiredBallPositionOnPlayerCar.toFlatVector());
        renderer.drawRectangle3d(Color.red, desiredBallPositionOnPlayerCar.toFlatVector(), 10, 10, true);
        renderer.drawRectangle3d(Color.magenta, smoothedOutDesiredBallPositionOnPlayerCar.plus(input.car.position).toFlatVector(), 10, 10, true);

        renderer.drawLine3d(Color.yellow, neutralBallDestination.toFlatVector(), input.car.position.toFlatVector());
        renderer.drawRectangle3d(Color.yellow, neutralBallDestination.toFlatVector(), 10, 10, true);

        //renderer.drawLine3d(Color.red, steeringDestination, input.car.position);
    }
}
