package rlbotexample.bot_behaviour.skill_controller.test_controller;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.input.dynamic_data.DataPacket;
import util.controllers.PidController;
import util.parameter_configuration.PidSerializer;
import util.math.vector.Vector3;

import java.awt.*;

public class AirDribbleTest2 extends SkillController {

    private static final double MAXIMUM_TARGET_BALL_SPEED = 400;
    private static final double MAXIMUM_BALL_OFFSET = 0;

    private PidController playerDestinationOffsetXPid;
    private PidController playerDestinationOffsetYPid;
    private PidController playerDestinationOffsetZPid;

    private PidController playerOrientationXPid;
    private PidController playerOrientationYPid;
    private PidController playerOrientationZPid;

    private PidController pitchPid;
    private PidController yawPid;
    private PidController rollPid;

    private PidController aerialBoostPid;

    private BotBehaviour bot;

    private Vector3 playerOrientationVector;
    private Vector3 playerDestination;
    private Vector3 playerNosePosition;

    private JumpHandler jumpHandler;

    public AirDribbleTest2(BotBehaviour bot) {
        super();
        this.bot = bot;

        playerDestinationOffsetXPid = new PidController(0.01, 0, 0.05);
        playerDestinationOffsetYPid = new PidController(0.01, 0, 0.05);
        playerDestinationOffsetZPid = new PidController(0, 0, 0);

        playerOrientationXPid = new PidController(0.05, 0, 0.2);
        playerOrientationYPid = new PidController(0.05, 0, 0.2);
        playerOrientationZPid = new PidController(0.02, 0, 0.1);
        aerialBoostPid = new PidController(1, 0, 0);

        pitchPid = new PidController(6, 0, 60);
        yawPid = new PidController(6, 0, 60);
        rollPid = new PidController(4.3, 0, 14);

        playerOrientationVector = new Vector3();
        playerDestination = new Vector3();
        playerNosePosition = new Vector3();

        jumpHandler = new JumpHandler();
    }

    @Override
    public void updateOutput(DataPacket input) {
        findDesiredAerialDirection(input);

        updateAerialOutput(input);

        updateJumpBehaviour(input);
    }

    private void findDesiredAerialDirection(DataPacket input) {
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

        playerOrientationXPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ORIENTATION_XY_FILENAME, playerOrientationXPid);
        playerOrientationYPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ORIENTATION_XY_FILENAME, playerOrientationYPid);
        playerOrientationZPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ORIENTATION_XY_FILENAME, playerOrientationZPid);

        aerialBoostPid = PidSerializer.fromFileToPid(PidSerializer.AERIAL_BOOST_FILENAME, aerialBoostPid);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position, playerOrientationVector);
        renderer.drawRectangle3d(Color.green, playerNosePosition, 10, 10, true);
        renderer.drawLine3d(Color.orange, input.ball.position, playerDestination);
        renderer.drawRectangle3d(Color.orange, playerDestination, 10, 10, true);
    }
}
