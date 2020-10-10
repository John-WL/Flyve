package rlbotexample.bot_behaviour.skill_controller;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.input.dynamic_data.DataPacket;
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
        // note: the "1000" here in the max function is arbitrary. Actually, this value is being tweaked by the proportional
        // parameter in the pid controllers x and y. Scale the proportional factor up and the 1000 now seem to be closer.
        // Scale it down and it seems farther away.

        // normalize the vector to some arbitrary length so the pid can handle properly
    }

    private void updateAerialOutput(DataPacket input) {
    }

    private void updateJumpBehaviour(DataPacket input) {
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
