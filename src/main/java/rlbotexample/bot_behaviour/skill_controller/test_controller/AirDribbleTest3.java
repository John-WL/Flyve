package rlbotexample.bot_behaviour.skill_controller.test_controller;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.SimpleJump;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.game_constants.RlConstants;
import util.parameter_configuration.ArbitraryValueSerializer;
import util.parameter_configuration.PidSerializer;
import parameter_search.air_dribbling.AirDribbleParameterSearcherFileData;
import util.vector.Vector2;
import util.vector.Vector3;

import java.awt.*;

public class AirDribbleTest3 extends SkillController {

    private static final double MAXIMUM_TARGET_BALL_SPEED = 400;

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

    private double noseDistanceFromPLayer;
    private double ballRadiusCoefficient;
    private double displacementAmountCoefficient;
    private double maximumBallOffset;

    public AirDribbleTest3(BotBehaviour bot) {
        super();
        this.bot = bot;

        playerDestinationOffsetXPid = new PidController(0.05, 0, 0.01);
        playerDestinationOffsetYPid = new PidController(0.05, 0, 0.01);
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

        noseDistanceFromPLayer = 60;
        ballRadiusCoefficient = 0.9;
        displacementAmountCoefficient = 1500;
        maximumBallOffset = 30;
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


        aerialBoostPid = PidSerializer.fromFileToPid(AirDribbleParameterSearcherFileData.AERIAL_BOOST_ROOTED_FILENAME, aerialBoostPid);

        noseDistanceFromPLayer = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.AIR_DRIBBLE_NOSE_DISTANCE_FROM_PLAYER);
        ballRadiusCoefficient = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.AIR_DRIBBLE_BALL_RADIUS_COEFFICIENT);

        playerDestinationOffsetXPid = PidSerializer.fromFileToPid(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_ROOTED_FILENAME, playerDestinationOffsetXPid);
        playerDestinationOffsetYPid = PidSerializer.fromFileToPid(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_ROOTED_FILENAME, playerDestinationOffsetYPid);
        //playerDestinationOffsetZPid = PidSerializer.fromFileToPid(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_ROOTED_FILENAME, playerDestinationOffsetZPid);

        playerOrientationXPid = PidSerializer.fromFileToPid(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_ORIENTATION_XY_ROOTED_FILENAME, playerOrientationXPid);
        playerOrientationYPid = PidSerializer.fromFileToPid(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_ORIENTATION_XY_ROOTED_FILENAME, playerOrientationYPid);
        playerOrientationZPid = PidSerializer.fromFileToPid(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_ORIENTATION_Z_ROOTED_FILENAME, playerOrientationZPid);

        displacementAmountCoefficient = ArbitraryValueSerializer.serialize(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_DISPLACEMENT_AMOUNT_COEFFICIENT_ROOTED_FILENAME);
        maximumBallOffset = Math.max(0, Math.min(92.75, ArbitraryValueSerializer.serialize(AirDribbleParameterSearcherFileData.AIR_DRIBBLE_MAXIMUM_BALL_OFFSET_ROOTED_FILENAME)));

        /*
        playerDestinationOffsetXPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ROOTED_FILENAME, playerDestinationOffsetXPid);
        playerDestinationOffsetYPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ROOTED_FILENAME, playerDestinationOffsetYPid);
        //playerDestinationOffsetZPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ROOTED_FILENAME, playerDestinationOffsetZPid);

        playerOrientationXPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ORIENTATION_XY_ROOTED_FILENAME, playerOrientationXPid);
        playerOrientationYPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ORIENTATION_XY_ROOTED_FILENAME, playerOrientationYPid);
        playerOrientationZPid = PidSerializer.fromFileToPid(PidSerializer.AIR_DRIBBLE_ORIENTATION_Z_ROOTED_FILENAME, playerOrientationZPid);

        displacementAmountCoefficient = ArbitraryValueSerializer.serialize(ArbitraryValueSerializer.AIR_DRIBBLE_DISPLACEMENT_AMOUNT_COEFFICIENT_ROOTED_FILENAME);
        */
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position, playerOrientationVector);
        renderer.drawRectangle3d(Color.green, playerNosePosition, 10, 10, true);
        renderer.drawLine3d(Color.orange, input.ball.position, playerDestination);
        renderer.drawRectangle3d(Color.orange, playerDestination, 10, 10, true);
    }
}
