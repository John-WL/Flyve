package rlbotexample.bot_behaviour.skill_controller.trash;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.SimpleJump;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.parameter_configuration.PidSerializer;
import util.controllers.PidController;
import util.math.vector.Vector3;

public class FlyToDestination extends SkillController {

    //private static final double SPEED_FACTOR_FOR_AERIAL_PID_CONTROLLERS = 24;
    private static final double SPEED_FACTOR_FOR_AERIAL_PID_CONTROLLERS = 0;

    private PidController pitchPid;
    private PidController yawPid;
    private PidController rollPid;

    private PidController aerialOrientationXPid;
    private PidController aerialOrientationYPid;
    private PidController aerialBoostPid;

    private BotBehaviour bot;

    private Vector3 lastAerialDestination;

    private JumpHandler jumpHandler;

    Vector3 aerialDestination;

    public FlyToDestination(BotBehaviour bot) {
        super();
        this.bot = bot;

        aerialOrientationXPid = new PidController(2, 0, 0.1);
        aerialOrientationYPid = new PidController(2, 0, 0.1);
        aerialBoostPid = new PidController(100000, 0, 0);

        pitchPid = new PidController(200, 0, 5000);
        yawPid = new PidController(200, 0, 5000);
        rollPid = new PidController(200, 0, 5000);

        lastAerialDestination = new Vector3();

        jumpHandler = new JumpHandler();

        aerialDestination = new Vector3();
    }

    @Override
    public void updateOutput(DataPacket input) {
        findDesiredAerialDirection(input);

        updateAerialOutput(input);

        updateJumpBehaviour(input);
    }

    private void findDesiredAerialDirection(DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 mySpeed = input.car.velocity;
    }

    private void updateAerialOutput(DataPacket input) {
        BotOutput output = bot.output();


    }

    private void updateJumpBehaviour(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 mySpeed = input.car.velocity;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;

        if (jumpHandler.isJumpFinished()) {
            if(input.car.hasWheelContact) {
                jumpHandler.setJumpType(new SimpleJump());
            }
            else {
                jumpHandler.setJumpType(new Wait());
            }
        }
        output.jump(jumpHandler.getJumpState());
    }

    @Override
    public void setupController() {
        pitchPid = PidSerializer.fromFileToPid(PidSerializer.PITCH_YAW_FILENAME, pitchPid);
        yawPid = PidSerializer.fromFileToPid(PidSerializer.PITCH_YAW_FILENAME, yawPid);
        rollPid = PidSerializer.fromFileToPid(PidSerializer.ROLL_FILENAME, rollPid);
        aerialOrientationXPid = PidSerializer.fromFileToPid(PidSerializer.AERIAL_ANGLE_FILENAME, aerialOrientationXPid);
        aerialOrientationYPid = PidSerializer.fromFileToPid(PidSerializer.AERIAL_ANGLE_FILENAME, aerialOrientationYPid);
        aerialBoostPid = PidSerializer.fromFileToPid(PidSerializer.AERIAL_BOOST_FILENAME, aerialBoostPid);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
