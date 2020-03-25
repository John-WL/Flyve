package rlbotexample.bot_behaviour.bot_movements;

import rlbotexample.bot_behaviour.BotBehaviour;
import rlbotexample.bot_behaviour.bot_movements.jump.*;
import rlbotexample.bot_behaviour.car_destination.*;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.parameter_configuration.ArbitraryValueSerializer;
import util.parameter_configuration.PidSerializer;
import util.pid_controller.PidController;
import util.timer.Clock;
import util.timer.Timer;
import util.vector.Vector2;
import util.vector.Vector3;


public class MovementOutputHandler {

    private static final double TIME_BEFORE_RELOADING_PIDS = 1;

    private BotBehaviour bot;
    private CarDestination desiredDestination;
    private PidController throttlePid;
    private PidController steerPid;
    private PidController aerialOrientationXPid;
    private PidController aerialOrientationYPid;
    private PidController pitchPid;
    private PidController yawPid;
    private PidController rollPid;
    private PidController aerialBoostPid;

    private JumpHandler jumpHandler;

    private boolean isAerialing;

    private Timer pidParamReloadTime;
    private double boostForThrottleThreshold = 1;
    private double driftForSteerThreshold = 1;

    private Clock accelerationClock;

    public MovementOutputHandler(CarDestination desiredDestination, BotBehaviour bot) {
        this.desiredDestination = desiredDestination;
        this.bot = bot;

        this.isAerialing = false;

        // pid presets for a 30 fps refresh rate
        throttlePid = new PidController(5, 0, 10);
        steerPid = new PidController(0.02, 0, 0.04);

        aerialOrientationXPid = new PidController(2, 0, 0.1);
        aerialOrientationYPid = new PidController(2, 0, 0.1);
        aerialBoostPid = new PidController(100000, 0, 0);

        pitchPid = new PidController(200, 0, 5000);
        yawPid = new PidController(200, 0, 5000);
        rollPid = new PidController(200, 0, 5000);

        jumpHandler = new JumpHandler();

        pidParamReloadTime = new Timer(TIME_BEFORE_RELOADING_PIDS);
        pidParamReloadTime.start();

        accelerationClock = new Clock();
        accelerationClock.start();
    }

    public void actualizeBotOutput(DataPacket input) {
        // actualize the pid values
        if(pidParamReloadTime.isTimeElapsed()) {
            pidParamReloadTime.start();
            throttlePid = PidSerializer.serialize(PidSerializer.THROTTLE_FILENAME);
            steerPid = PidSerializer.serialize(PidSerializer.STEERING_FILENAME);
            pitchPid = PidSerializer.serialize(PidSerializer.PITCH_YAW_ROLL_FILENAME);
            yawPid = PidSerializer.serialize(PidSerializer.PITCH_YAW_ROLL_FILENAME);
            rollPid = PidSerializer.serialize(PidSerializer.PITCH_YAW_ROLL_FILENAME);
            aerialOrientationXPid = PidSerializer.serialize(PidSerializer.AERIAL_ANGLE_FILENAME);
            aerialOrientationYPid = PidSerializer.serialize(PidSerializer.AERIAL_ANGLE_FILENAME);
            aerialBoostPid = PidSerializer.serialize(PidSerializer.AERIAL_BOOST_FILENAME);

            boostForThrottleThreshold = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.BOOST_FOR_THROTTLE_THRESHOLD_FILENAME);
            driftForSteerThreshold = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.DRIFT_FOR_STEERING_THRESHOLD_FILENAME);
        }

        BotOutput output = bot.output();

        Vector3 myPosition = input.car.position;
        Vector3 mySpeed = input.car.velocity;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;
        Vector3 ballPosition = input.ball.position;
        Vector3 ballSpeed = input.ball.velocity;

        Vector3 myDestination = desiredDestination.getThrottleDestination();
        Vector3 myLocalDestination = CarDestination.getLocal(myDestination, input);
        Vector3 myPreviousLocalDestination = CarDestination.getLocal(desiredDestination.getPreviousThrottleDestination(), input);
        Vector3 mySteeringDestination = desiredDestination.getSteeringDestination(input);
        Vector3 myPreviousSteeringDestination = desiredDestination.getPreviousSteeringDestination();
        Vector3 myLocalSteeringDestination = CarDestination.getLocal(mySteeringDestination, input);
        Vector3 myPreviousLocalSteeringDestination = CarDestination.getLocal(desiredDestination.getPreviousThrottleDestination(), input);
        Vector3 myAerialDestination = desiredDestination.getAerialDestination();
        Vector3 myLocalAerialDestination = CarDestination.getLocal(myAerialDestination, input);
        Vector3 myPreviousLocalAerialDestination = CarDestination.getLocal(desiredDestination.getPreviousAerialDestination(), input);



        // throttling
        // double throttleAmount = throttlePid.process(myLocalDestination.x, 0);
        //double throttleAmount = throttlePid.process(myDestination.minus(myPosition).minusAngle(myNoseVector).x, mySpeed.minusAngle(myNoseVector).x / 50);
        //if(myLocalSteeringDestination.x < 0) throttleAmount = -throttleAmount;

        double throttleAmount = throttlePid.process(myLocalDestination.x, 0);

        // steering
        Vector2 myLocalSteeringDestination2D = myLocalSteeringDestination.flatten();
        Vector2 desiredLocalSteeringVector = new Vector2(1, 0);
        double steeringCorrectionAngle = myLocalSteeringDestination2D.correctionAngle(desiredLocalSteeringVector);
        double steerAmount = steerPid.process(steeringCorrectionAngle, 0);

        output.throttle(throttleAmount);
        output.boost(throttleAmount > boostForThrottleThreshold);

        output.steer(steerAmount);
        output.drift(Math.abs(steerAmount) > driftForSteerThreshold);


        // aerialing
        /*
        double myAerialDestinationX = -aerialOrientationXPid.process(mySpeed.x, myDestination.minus(myPosition).x);
        double myAerialDestinationY = -aerialOrientationYPid.process(mySpeed.y, myDestination.minus(myPosition).y);
        double myAerialDestinationZ = myDestination.minus(myPosition).magnitude()*2 + 100;
        desiredDestination.setAerialDestination(myDestination.plus(new Vector3(myAerialDestinationX, myAerialDestinationY, myAerialDestinationZ)));
        */
        /*
        double myAerialDestinationX = aerialOrientationXPid.process(myDestination.x, myPosition.x); // X
        double myAerialDestinationY = aerialOrientationYPid.process(myDestination.y, myPosition.y); // Y
        Vector2 myAerialDestinationXY = new Vector2(myAerialDestinationX, myAerialDestinationY);
        double myAerialDestinationLengthXY = myAerialDestinationXY.magnitude();
        double myAerialDestinationZ = Math.max(1000, myAerialDestinationLengthXY);                  // Z
        // note: the "1000" here in the max function arbitrary. Actually, this value is being tweaked by the proportional
        // parameter in the pid controllers x and y. Scale the proportional factor up and the 1000 now seem to be closer.
        // Scale it down and it seem farther away.
        desiredDestination.setAerialDestination(myPosition.plus(new Vector3(myAerialDestinationX, myAerialDestinationY, myAerialDestinationZ)));
        */

        // aerial desired direction
        double myAerialDestinationX = aerialOrientationXPid.process(myDestination.minus(myPosition).x, mySpeed.x); // X
        double myAerialDestinationY = aerialOrientationYPid.process(myDestination.minus(myPosition).y, mySpeed.y); // Y
        Vector2 myAerialDestinationXY = new Vector2(myAerialDestinationX, myAerialDestinationY);
        double myAerialDestinationLengthXY = myAerialDestinationXY.magnitude();
        double myAerialDestinationZ = Math.max(1000, myAerialDestinationLengthXY);                  // Z
        // note: the "1000" here in the max function arbitrary. Actually, this value is being tweaked by the proportional
        // parameter in the pid controllers x and y. Scale the proportional factor up and the 1000 now seem to be closer.
        // Scale it down and it seem farther away.
        desiredDestination.setAerialDestination(myPosition.plus(new Vector3(myAerialDestinationX, myAerialDestinationY, myAerialDestinationZ)));

        isAerialing = false;
        if(ballSpeed.z + ballPosition.z > 800) {
            isAerialing = true;
        }


        // pitch yaw and roll orientations...
        double pitchAmount;
        double yawAmount;
        double rollAmount;
        if(isAerialing) {
            pitchAmount = pitchPid.process(myLocalAerialDestination.z, 0);
            yawAmount = yawPid.process(-myLocalAerialDestination.y, 0);
            rollAmount = rollPid.process(myLocalAerialDestination.x, 0);
            boolean aerialBoostState = aerialBoostPid.process(myDestination.minus(myPosition).z, mySpeed.z) > 0;
            output.boost(aerialBoostState);
        }
        else {
            pitchAmount = pitchPid.process(myLocalSteeringDestination.z, 0);
            yawAmount = yawPid.process(-myLocalSteeringDestination.y, 0);
            rollAmount = rollPid.process(myLocalSteeringDestination.x, 0);
        }

        output.pitch(pitchAmount);
        output.yaw(yawAmount);
        //output.roll(rollAmount);
        // jumping
        if (jumpHandler.isJumpFinished()) {
            jumpHandler.setJumpType(new SimpleJump());
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

    public boolean isAerialing() {
        return isAerialing;
    }
}
