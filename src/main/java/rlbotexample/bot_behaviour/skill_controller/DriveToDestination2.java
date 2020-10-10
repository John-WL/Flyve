package rlbotexample.bot_behaviour.skill_controller;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.HalfFlip;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.SimpleJump;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.Wait;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.parameter_configuration.ArbitraryValueSerializer;
import util.parameter_configuration.PidSerializer;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class DriveToDestination2 extends SkillController {

    private PidController throttlePid;
    private PidController steerPid;

    private PidController pitchPid;
    private PidController yawPid;
    private PidController rollPid;

    private JumpHandler jumpHandler;

    private BotBehaviour bot;

    private double boostForThrottleThreshold = 1;
    private double driftForSteerThreshold = 1;

    public DriveToDestination2(BotBehaviour bot) {
        super();
        this.bot = bot;

        throttlePid = new PidController(5, 0, 10);
        steerPid = new PidController(0.02, 0, 0.04);

        pitchPid = new PidController(200, 0, 5000);
        yawPid = new PidController(200, 0, 5000);
        rollPid = new PidController(200, 0, 5000);

        jumpHandler = new JumpHandler();
    }

    @Override
    public void updateOutput(DataPacket input) {
        // drive and turn to reach destination F
        throttle(input);
        steer(input);

        // for when the car is accidentally in the air...
        // this allows for a basic handling of air control in the
        // case that the car happens to be in mid-air when this class is used
        pitchYawRoll(input);

        // stop boosting if supersonic
        preventUselessBoost(input);

        // halfFlips and stuff
        updateJumpBehaviour(input);
    }

    private void throttle(DataPacket input) {
        // get useful variables
        BotOutput output = bot.output();
        Vector3 playerPosition = input.car.position;
        Vector3 playerSpeed = input.car.velocity;

        // send the result to the botOutput controller
    }

    private void steer(DataPacket input) {
        // get useful variables
        BotOutput output = bot.output();

        // transform the destination into an angle so it's easier to handle with the pid
        Vector2 desiredLocalSteeringVector = new Vector2(1, 0);


        // send the result to the botOutput controller
    }

    private void pitchYawRoll(DataPacket input) {
        // get useful variables
        BotOutput output = bot.output();

        // compute the pitch, roll, and yaw pid values

        // send the result to the botOutput controller
        //output.roll(rollAmount);
    }

    private void preventUselessBoost(DataPacket input) {
        // get useful values
        BotOutput output = bot.output();
        Vector3 playerPosition = input.car.position;
        Vector3 playerSpeed = input.car.velocity;
        Vector3 ballPosition = input.ball.position;

        if(playerSpeed.magnitude() >= 2200) {
            output.boost(false);
        }
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
        output.jump(jumpHandler.getJumpState());
    }

    @Override
    public void setupController() {
        // instantiate new pid controllers based on the data files that corresponds
        throttlePid = PidSerializer.fromFileToPid(PidSerializer.THROTTLE_FILENAME, throttlePid);
        steerPid = PidSerializer.fromFileToPid(PidSerializer.STEERING_FILENAME, steerPid);
        pitchPid = PidSerializer.fromFileToPid(PidSerializer.PITCH_YAW_FILENAME, pitchPid);
        yawPid = PidSerializer.fromFileToPid(PidSerializer.PITCH_YAW_FILENAME, yawPid);
        rollPid = PidSerializer.fromFileToPid(PidSerializer.ROLL_FILENAME, rollPid);

        boostForThrottleThreshold = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.BOOST_FOR_THROTTLE_THRESHOLD_FILENAME);
        driftForSteerThreshold = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.DRIFT_FOR_STEERING_THRESHOLD_FILENAME);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
