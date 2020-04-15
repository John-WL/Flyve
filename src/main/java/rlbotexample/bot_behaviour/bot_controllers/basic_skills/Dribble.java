package rlbotexample.bot_behaviour.bot_controllers.basic_skills;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.BotBehaviour;
import rlbotexample.bot_behaviour.bot_controllers.jump.JumpHandler;
import rlbotexample.bot_behaviour.bot_controllers.jump.implementations.HalfFlip;
import rlbotexample.bot_behaviour.bot_controllers.jump.implementations.SimpleJump;
import rlbotexample.bot_behaviour.bot_controllers.jump.implementations.Wait;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.parameter_configuration.ArbitraryValueSerializer;
import util.parameter_configuration.PidSerializer;
import util.controllers.PidController;
import util.controllers.ThrottleController;
import util.vector.Vector2;
import util.vector.Vector3;

import java.awt.*;

public class Dribble extends OutputUpdater  {

    private static final double MAXIMUM_TARGET_BALL_SPEED = 1500;
    private static final double MINIMUM_TARGET_BALL_SPEED = 50;
    private static final double MAXIMUM_BALL_OFFSET = 65;

    private CarDestination desiredDestination;
    private BotBehaviour bot;

    private PidController ballDirectionXPid;
    private PidController ballDirectionYPid;

    private PidController throttlePid;
    private PidController steerPid;

    private PidController pitchPid;
    private PidController yawPid;
    private PidController rollPid;

    private Vector3 dribblingDestination;
    private Vector3 dribblingSteeringDestination;

    private double boostForThrottleThreshold = 1;
    private double driftForSteerThreshold = 1;

    private JumpHandler jumpHandler;

    public Dribble(CarDestination desiredDestination, BotBehaviour bot) {
        super();
        this.desiredDestination = desiredDestination;
        this.bot = bot;

        ballDirectionXPid = new PidController(0.03, 0, 0.005);
        ballDirectionYPid = new PidController(0.03, 0, 0.005);

        throttlePid = new PidController(1, 0, 0);
        steerPid = new PidController(1, 0, 0);

        pitchPid = new PidController(200, 0, 5000);
        yawPid = new PidController(200, 0, 5000);
        rollPid = new PidController(200, 0, 5000);

        jumpHandler = new JumpHandler();
    }

    @Override
    void updateOutput(DataPacket input) {

        // get useful values
        BotOutput output = bot.output();
        Vector3 playerSpeed = input.car.velocity;
        Vector3 playerNoseOrientation = input.car.orientation.noseVector;
        Vector3 ballPosition = input.ball.position;
        Vector3 ballSpeed = input.ball.velocity;
        Vector3 ballDestination = desiredDestination.getThrottleDestination();
        Vector3 ballSteeringDestination = desiredDestination.getSteeringDestination();

        Vector2 cappedTargetBallSpeed = new Vector2(
                Math.max(-MAXIMUM_TARGET_BALL_SPEED, Math.min(MAXIMUM_TARGET_BALL_SPEED, ballPosition.minus(ballDestination).x)),
                Math.max(-MAXIMUM_TARGET_BALL_SPEED, Math.min(MAXIMUM_TARGET_BALL_SPEED, ballPosition.minus(ballDestination).y))
        );

        // compute the desired offset from the ball to be able to accelerate or slow down, turn left or right accordingly...
        double desiredPlayerOffsetX = ballDirectionXPid.process(cappedTargetBallSpeed.x, -ballSpeed.x);
        double desiredPlayerOffsetY = ballDirectionYPid.process(cappedTargetBallSpeed.y, -ballSpeed.y);

        desiredPlayerOffsetX = Math.max(-MAXIMUM_BALL_OFFSET, Math.min(MAXIMUM_BALL_OFFSET, desiredPlayerOffsetX));
        desiredPlayerOffsetY = Math.max(-MAXIMUM_BALL_OFFSET, Math.min(MAXIMUM_BALL_OFFSET, desiredPlayerOffsetY));

        // compute the actual player destination
        Vector3 desiredPlayerOffset = new Vector3(desiredPlayerOffsetX, desiredPlayerOffsetY, 0);
        Vector3 playerDestination = ballPosition.plus(desiredPlayerOffset);
        dribblingDestination = playerDestination;

        // transform it into the player's local coordinate system
        Vector3 localDestination = CarDestination.getLocal(playerDestination, input);

        // compute the throttle value
        double throttleAmount = -throttlePid.process(playerSpeed.minus(ballSpeed).minusAngle(playerNoseOrientation).x, localDestination.x*10);
        throttleAmount = ThrottleController.process(throttleAmount);
        // compute the steering destination from ball speed and throttle destination
        // here, we steer in the direction of the ball's velocity.
        Vector3 steeringDestination = playerDestination.plus(ballSpeed.scaledToMagnitude(180));
        dribblingSteeringDestination = steeringDestination;

        // transform it into the player's local coordinate system
        Vector3 localSteeringDestination = CarDestination.getLocal(steeringDestination, input);

        // transform the destination into an angle so it's easier to handle with the pid
        Vector2 myLocalSteeringDestination2D = localSteeringDestination.flatten();
        Vector2 desiredLocalSteeringVector = new Vector2(1, 0);
        double steeringCorrectionAngle = myLocalSteeringDestination2D.correctionAngle(desiredLocalSteeringVector);

        // compute the steer value
        double steerAmount = steerPid.process(steeringCorrectionAngle, 0);

        output.throttle(throttleAmount);
        output.boost(throttleAmount > boostForThrottleThreshold);
        if(playerSpeed.magnitude() == 2300) {
            output.boost(false);
        }
        output.steer(steerAmount);
        output.drift(Math.abs(steerAmount) > driftForSteerThreshold);

        /*
        // get useful values
        BotOutput output = bot.output();
        Vector3 playerSpeed = input.car.velocity;
        Vector3 playerNoseOrientation = input.car.orientation.noseVector;
        Vector3 ballPosition = input.ball.position;
        Vector3 ballSpeed = input.ball.velocity;
        Vector3 ballDestination = desiredDestination.getThrottleDestination();
        Vector3 ballSteeringDestination = desiredDestination.getSteeringDestination();

        Vector2 cappedTargetBallSpeed = new Vector2(
                Math.max(-MAXIMUM_TARGET_BALL_SPEED, Math.min(MAXIMUM_TARGET_BALL_SPEED, ballPosition.minus(ballDestination).x)),
                Math.max(-MAXIMUM_TARGET_BALL_SPEED, Math.min(MAXIMUM_TARGET_BALL_SPEED, ballPosition.minus(ballDestination).y))
        );

        // compute the desired offset from the ball to be able to accelerate or slow down, turn left or right accordingly...
        double desiredPlayerOffsetX = ballDirectionXPid.process(cappedTargetBallSpeed.x, -ballSpeed.x);
        double desiredPlayerOffsetY = ballDirectionYPid.process(cappedTargetBallSpeed.y, -ballSpeed.y);

        desiredPlayerOffsetX = Math.max(-MAXIMUM_BALL_OFFSET, Math.min(MAXIMUM_BALL_OFFSET, desiredPlayerOffsetX));
        desiredPlayerOffsetY = Math.max(-MAXIMUM_BALL_OFFSET, Math.min(MAXIMUM_BALL_OFFSET, desiredPlayerOffsetY));

        // compute the actual player destination
        Vector3 desiredPlayerOffset = new Vector3(desiredPlayerOffsetX, desiredPlayerOffsetY, 0);
        Vector3 playerDestination = ballPosition.plus(desiredPlayerOffset);
        dribblingDestination = playerDestination;

        // transform it into the player's local coordinate system
        Vector3 localDestination = CarDestination.getLocal(playerDestination, input);

        // compute the throttle value
        double throttleAmount = -throttlePid.process(playerSpeed.minus(ballSpeed).minusAngle(playerNoseOrientation).x, localDestination.x*10);
        throttleAmount = ThrottleController.process(throttleAmount);
        // compute the steering destination from ball speed and throttle destination
        // here, we steer in the direction of the ball's velocity.
        Vector3 steeringDestination = playerDestination.plus(ballSpeed.scaledToMagnitude(180));
        dribblingSteeringDestination = steeringDestination;

        // transform it into the player's local coordinate system
        Vector3 localSteeringDestination = CarDestination.getLocal(steeringDestination, input);

        // transform the destination into an angle so it's easier to handle with the pid
        Vector2 myLocalSteeringDestination2D = localSteeringDestination.flatten();
        Vector2 desiredLocalSteeringVector = new Vector2(1, 0);
        double steeringCorrectionAngle = myLocalSteeringDestination2D.correctionAngle(desiredLocalSteeringVector);

        // compute the steer value
        double steerAmount = steerPid.process(steeringCorrectionAngle, 0);

        output.throttle(throttleAmount);
        output.boost(throttleAmount > boostForThrottleThreshold);
        if(playerSpeed.magnitude() == 2300) {
            output.boost(false);
        }
        output.steer(steerAmount);
        output.drift(Math.abs(steerAmount) > driftForSteerThreshold);
        */

        pitchYawRoll(input);
        updateJumpBehaviour(input);
    }

    private void pitchYawRoll(DataPacket input) {
        // get useful variables
        BotOutput output = bot.output();
        Vector3 ballPosition = input.ball.position;
        Vector3 localballPosition = CarDestination.getLocal(ballPosition, input);

        // compute the pitch, roll, and yaw pid values
        double pitchAmount = pitchPid.process(localballPosition.z, 0);
        double yawAmount = yawPid.process(-localballPosition.y, 0);
        double rollAmount = rollPid.process(localballPosition.x, 0);

        // send the result to the botOutput controller
        output.pitch(pitchAmount);
        output.yaw(yawAmount);
        //output.roll(rollAmount);
    }

    private void updateJumpBehaviour(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 mySpeed = input.car.velocity;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;

        if (jumpHandler.isJumpFinished()) {
            if(mySpeed.minusAngle(myNoseVector).x < -200) {
                if(input.car.hasWheelContact) {
                    jumpHandler.setJumpType(new SimpleJump());
                }
                else {
                    jumpHandler.setJumpType(new HalfFlip());
                }
            }
            else {
                jumpHandler.setJumpType(new Wait());
            }
        }
        jumpHandler.updateJumpState(
                input,
                output,
                CarDestination.getLocal(
                        desiredDestination.getThrottleDestination(),
                        input
                ),
                myRoofVector.minusAngle(new Vector3(0, 0, 1))
        );
        output.jump(jumpHandler.getJumpState());
    }

    @Override
    void updatePidValuesAndArbitraries() {
        throttlePid = PidSerializer.serialize(PidSerializer.THROTTLE_FILENAME, throttlePid);
        steerPid = PidSerializer.serialize(PidSerializer.STEERING_FILENAME, steerPid);
        pitchPid = PidSerializer.serialize(PidSerializer.PITCH_YAW_ROLL_FILENAME, pitchPid);
        yawPid = PidSerializer.serialize(PidSerializer.PITCH_YAW_ROLL_FILENAME, yawPid);
        rollPid = PidSerializer.serialize(PidSerializer.PITCH_YAW_ROLL_FILENAME, rollPid);

        boostForThrottleThreshold = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.BOOST_FOR_THROTTLE_THRESHOLD_FILENAME);
        driftForSteerThreshold = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.DRIFT_FOR_STEERING_THRESHOLD_FILENAME);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.ORANGE, new Vector3(dribblingDestination.flatten().x, dribblingDestination.flatten().y, input.ball.position.z + 100), new Vector3(input.ball.position.flatten().x, input.ball.position.flatten().y, input.ball.position.z + 100));
        renderer.drawCenteredRectangle3d(Color.ORANGE, new Vector3(dribblingDestination.flatten().x, dribblingDestination.flatten().y, input.ball.position.z + 100), 10, 10, true);
        renderer.drawLine3d(Color.red, dribblingSteeringDestination, input.car.position);
    }
}
