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
import util.vector.Vector2;
import util.vector.Vector3;

import java.awt.*;

public class DriveToDestination extends OutputUpdater {

    private PidController throttlePid;
    private PidController steerPid;

    private PidController pitchPid;
    private PidController yawPid;
    private PidController rollPid;

    private JumpHandler jumpHandler;

    private CarDestination desiredDestination;
    private BotBehaviour bot;

    private double boostForThrottleThreshold = 1;
    private double driftForSteerThreshold = 1;

    public DriveToDestination(CarDestination desiredDestination, BotBehaviour bot) {
        super();
        this.desiredDestination = desiredDestination;
        this.bot = bot;

        throttlePid = new PidController(5, 0, 10);
        steerPid = new PidController(0.02, 0, 0.04);

        pitchPid = new PidController(200, 0, 5000);
        yawPid = new PidController(200, 0, 5000);
        rollPid = new PidController(200, 0, 5000);

        jumpHandler = new JumpHandler();
    }

    @Override
    void updateOutput(DataPacket input) {
        // drive and turn to reach destination F
        throttle(input);
        steer(input);

        // for when the car is accidentally in the air...
        // this allows for a basic handling of air control in the
        // case that the car happens to be in mid-air when this class is used
        pitchYawRoll(input);

        // halfFlips and stuff
        updateJumpBehaviour(input);
    }

    private void throttle(DataPacket input) {
        // get useful variables
        BotOutput output = bot.output();
        Vector3 myDestination = desiredDestination.getThrottleDestination();
        Vector3 myLocalDestination = CarDestination.getLocal(myDestination, input);

        // compute the pid value for throttle
        double throttleAmount = throttlePid.process(myLocalDestination.x, 0);

        // send the result to the botOutput controller
        output.throttle(throttleAmount);
        output.boost(throttleAmount > boostForThrottleThreshold);
    }

    private void steer(DataPacket input) {
        // get useful variables
        BotOutput output = bot.output();
        Vector3 mySteeringDestination = desiredDestination.getSteeringDestination(input);
        Vector3 myLocalSteeringDestination = CarDestination.getLocal(mySteeringDestination, input);

        // transform the destination into an angle so it's easier to handle with the pid
        Vector2 myLocalSteeringDestination2D = myLocalSteeringDestination.flatten();
        Vector2 desiredLocalSteeringVector = new Vector2(1, 0);
        double steeringCorrectionAngle = myLocalSteeringDestination2D.correctionAngle(desiredLocalSteeringVector);

        // compute the pid value for steering
        double steerAmount = steerPid.process(steeringCorrectionAngle, 0);

        // send the result to the botOutput controller
        output.steer(steerAmount);
        output.drift(Math.abs(steerAmount) > driftForSteerThreshold);
    }

    private void pitchYawRoll(DataPacket input) {
        // get useful variables
        BotOutput output = bot.output();
        Vector3 mySteeringDestination = desiredDestination.getSteeringDestination(input);
        Vector3 myLocalSteeringDestination = CarDestination.getLocal(mySteeringDestination, input);

        // compute the pitch, roll, and yaw pid values
        double pitchAmount = pitchPid.process(myLocalSteeringDestination.z, 0);
        double yawAmount = yawPid.process(-myLocalSteeringDestination.y, 0);
        double rollAmount = rollPid.process(myLocalSteeringDestination.x, 0);

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
        // instantiate new pid controllers based on the data files that corresponds...
        // watch out, the integral value would be reset every time this function is called.
        // We're still using those instantiation functions because we don't need the integral value at all
        // in the whole project muahahahaha!
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
    }
}
