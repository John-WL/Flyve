package rlbotexample.bot_behaviour.bot_movements;

import rlbotexample.bot_behaviour.BotBehaviour;
import rlbotexample.bot_behaviour.car_destination.*;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.pid_controller.PidController;
import util.vector.Vector2;
import util.vector.Vector3;


public class MovementOutputHandler {

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
    private boolean isFlipping;
    private boolean isAerialing;

    private Vector3 myPreviousSpeed;

    public MovementOutputHandler(CarDestination desiredDestination, BotBehaviour bot) {
        this.desiredDestination = desiredDestination;
        this.bot = bot;

        this.isFlipping = false;
        this.isAerialing = false;

        // pid presets for a 30 fps refresh rate
        throttlePid = new PidController(1, 0, 0);
        steerPid = new PidController(3, 0, 1.2);

        aerialOrientationXPid = new PidController(2, 0, 0.1);
        aerialOrientationYPid = new PidController(2, 0, 0.1);
        aerialBoostPid = new PidController(100000, 0, 0);

        pitchPid = new PidController(30, 0, 800);
        yawPid = new PidController(30, 0, 800);
        rollPid = new PidController(30, 0, 800);

        myPreviousSpeed = new Vector3();
    }

    public void actualizeBotOutput(DataPacket input) {
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
        double throttleAmount = throttlePid.process(myDestination.minus(myPosition).minusAngle(myNoseVector).x, mySpeed.minusAngle(myNoseVector).x / 50);
        if(myLocalSteeringDestination.x < 0) throttleAmount = -throttleAmount;
        myPreviousSpeed = mySpeed;

        // steering
        double steeringDistanceFactor = 1;
        Vector3 myProlongedLocalSteering = CarDestination.getLocal(mySteeringDestination.minus(myDestination).scaled(steeringDistanceFactor).plus(myDestination), input);
        Vector3 myPreviousProlongedLocalSteering = CarDestination.getLocal(myPreviousSteeringDestination.minus(myDestination).scaledToMagnitude(steeringDistanceFactor).plus(myDestination), input);

        double steerAmount = -steerPid.process(myProlongedLocalSteering.minusAngle(myPreviousProlongedLocalSteering).flatten().correctionAngle(new Vector2(1, 0)), myProlongedLocalSteering.flatten().correctionAngle(new Vector2(1, 0)));

        output.throttle(throttleAmount);
        output.boost(throttleAmount > 100000);

        output.steer(steerAmount);
        output.drift(Math.abs(steerAmount) > 5);

        // update the direction when on ground
        double myAerialDestinationX = -aerialOrientationXPid.process(mySpeed.x, myDestination.minus(myPosition).x);
        double myAerialDestinationY = -aerialOrientationYPid.process(mySpeed.y, myDestination.minus(myPosition).y);
        double myAerialDestinationZ = myDestination.minus(myPosition).magnitude()*2 + 100;
        desiredDestination.setAerialDestination(myDestination.plus(new Vector3(myAerialDestinationX, myAerialDestinationY, myAerialDestinationZ)));

        isAerialing = false;
        if(ballSpeed.z + ballPosition.z > 1000) {
            isAerialing = true;
        }

        if(!isAerialing) {
            double speedDivisor = Math.abs(myLocalSteeringDestination.angle(myNoseVector)) + 1;
            desiredDestination.setDesiredSpeed(CarDestinationUpdater.DEFAULT_CAR_SPEED_VALUE / speedDivisor);
        }

        double pitchAmount = pitchPid.process(myLocalSteeringDestination.z, 0);
        double yawAmount = yawPid.process(-myLocalSteeringDestination.y, 0);
        double rollAmount = rollPid.process(myLocalSteeringDestination.x, 0);

        if(isAerialing) {
            if(input.car.hasWheelContact) {
                output.jump(!output.jump());
            }
            pitchAmount = pitchPid.process(myLocalAerialDestination.z, 0);
            yawAmount = yawPid.process(-myLocalAerialDestination.y, 0);
            rollAmount = rollPid.process(myLocalAerialDestination.x, 0);
            boolean aerialBoostState = aerialBoostPid.process(myDestination.minus(myPosition).z, mySpeed.z) > 0;

            output.boost(aerialBoostState);
        }

        output.pitch(pitchAmount);
        output.yaw(yawAmount);
        //output.roll(rollAmount);
    }

    public boolean isAerialing() {
        return isAerialing;
    }
}
