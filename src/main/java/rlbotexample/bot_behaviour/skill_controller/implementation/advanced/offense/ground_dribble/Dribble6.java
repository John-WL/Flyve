package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.Dribble5;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class Dribble6 extends SkillController {

    public static final double MIN_POSITIVE_DRIBBLE_SPEED = 600;
    public static final double MIN_NEGATIVE_DRIBBLE_SPEED = -300;
    public static final double MAX_DRIBBLE_SPEED = 1800;
    public static final double MAX_STEER_AMOUNT = 1;

    private final BotBehaviour bot;

    private final Dribble5 dribbleController;

    private double throttleAmount;
    private double steerAmount;

    private Vector3 ballDestination;
    private Vector3 convergingDestination;
    private double targetSpeed;

    public Dribble6(BotBehaviour bot) {
        this.bot = bot;

        this.dribbleController = new Dribble5(bot);

        this.throttleAmount = 0;
        this.steerAmount = 0;

        this.ballDestination = new Vector3();
        this.convergingDestination = new Vector3();
        this.targetSpeed = 0;
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    public void setTargetSpeed(double targetSpeed) {
        this.targetSpeed = targetSpeed;
        if(targetSpeed < MIN_POSITIVE_DRIBBLE_SPEED && targetSpeed > MIN_NEGATIVE_DRIBBLE_SPEED) {
            this.targetSpeed = MIN_POSITIVE_DRIBBLE_SPEED;
        }
        if(targetSpeed > MAX_DRIBBLE_SPEED) {
            this.targetSpeed = MAX_DRIBBLE_SPEED;
        }
    }

    @Override
    public void updateOutput(DataPacket input) {
        findDestinationToConverge(input);
        findSteerAmount(input);
        findThrottleAmount(input);

        //throttleAmount = 0;
        //steerAmount = 0;

        dribbleController.steer(steerAmount);
        dribbleController.throttle(throttleAmount);
        dribbleController.updateOutput(input);
    }

    private void findDestinationToConverge(DataPacket input) {
        convergingDestination = ballDestination.minus(input.ball.position)
                .rotate(input.ball.velocity
                        .findRotator(ballDestination.minus(input.ball.position)).scaled(0))
                .plus(input.ball.position);
    }

    private void findSteerAmount(DataPacket input) {
        steerAmount = -input.ball.velocity.flatten()
                .correctionAngle(convergingDestination.minus(input.ball.position).flatten());

        if(steerAmount > MAX_STEER_AMOUNT) {
            steerAmount = MAX_STEER_AMOUNT;
        }
        else if(steerAmount < -MAX_STEER_AMOUNT) {
            steerAmount = -MAX_STEER_AMOUNT;
        }
    }

    private void findThrottleAmount(DataPacket input) {
        double strictMaxThrottleValue = (RlConstants.CAR_MAX_SPEED - input.ball.velocity.magnitude()) / RlConstants.CAR_MAX_SPEED;
        double rawThrottleValue = deltaV(input) / RlConstants.CAR_MAX_SPEED;
        rawThrottleValue /= 2;

        if(rawThrottleValue > strictMaxThrottleValue
                || rawThrottleValue < -strictMaxThrottleValue) {
            if(rawThrottleValue != 0) {
                rawThrottleValue /= Math.abs(rawThrottleValue);
            }
            else {
                rawThrottleValue = 1;
            }
            rawThrottleValue *= strictMaxThrottleValue;
        }

        throttleAmount = rawThrottleValue;
    }

    private double square(double x) {
        return x*x;
    }

    private double deltaV(DataPacket input) {
        return targetSpeed - input.ball.velocity.magnitude();
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        dribbleController.debug(renderer, input);
        renderer.drawLine3d(Color.GREEN, convergingDestination, input.ball.position);
    }
}
