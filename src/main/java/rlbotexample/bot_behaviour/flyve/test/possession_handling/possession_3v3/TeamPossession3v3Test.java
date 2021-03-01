package rlbotexample.bot_behaviour.flyve.test.possession_handling.possession_3v3;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.metagame.advanced_gamestate_info.AerialInfo;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRole;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRoleHandler3V3;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.setup.AerialSetupController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.Dribble2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.flips_and_flicks.Flick;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.Dribble3;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.Dribble4;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationHandler;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DriveToDestination;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DriveToPredictedBallBounceController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class TeamPossession3v3Test extends FlyveBot {

    private PlayerRoleHandler3V3 playerRoleHandler3V3;
    private PlayerRoleHandler3V3 opponentRoleHandler3V3;
    private ExtendedCarData offensivePlayer;
    private ExtendedCarData backerPlayer;
    private ExtendedCarData lastManPlayer;

    private DriveToPredictedBallBounceController driveToPredictedBallBounceController;
    private DriveToDestination driveToDestination2ControllerForBacker;
    private DriveToDestination driveToDestination2ControllerForLastMan;
    private AerialSetupController2 aerialSetupController;
    private AerialOrientationHandler aerialRecoveryController;
    private Dribble3 dribbleController;
    private Flick flickController;

    public TeamPossession3v3Test() {

        driveToPredictedBallBounceController = new DriveToPredictedBallBounceController(this);
        driveToDestination2ControllerForBacker = new DriveToDestination(this);
        driveToDestination2ControllerForLastMan = new DriveToDestination(this);
        aerialSetupController = new AerialSetupController2(this);
        aerialRecoveryController = new AerialOrientationHandler(this);
        dribbleController = new Dribble3(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        playerRoleHandler3V3 = new PlayerRoleHandler3V3(input, input.team);
        //opponentRoleHandler3V3 = new PlayerRoleHandler3V3(input, 1-input.team);
        offensivePlayer = playerRoleHandler3V3.getPlayerFromRole(PlayerRole.OFFENSIVE);
        backerPlayer = playerRoleHandler3V3.getPlayerFromRole(PlayerRole.BACKER);
        lastManPlayer = playerRoleHandler3V3.getPlayerFromRole(PlayerRole.LAST_MAN);

        if(offensivePlayer.playerIndex == input.playerIndex) {
            processInputOfOffensivePlayer(input, packet);
        }
        else if(backerPlayer.playerIndex == input.playerIndex) {
            processInputOfBackerPlayer(input, packet);
        }
        else {
            processInputOfLastManPlayer(input, packet);
        }

        // return the calculated bot output
        return super.output();
    }

    private void processInputOfOffensivePlayer(DataPacket input, GameTickPacket packet) {
        Vector3 destination = new Vector3(0, -5200 * ((input.team*2)-1), 500);
        dribbleController.setBallDestination(destination);
        dribbleController.setupAndUpdateOutput(input);
        /*if((opponentRoleHandler3V3.getPlayerFromRole(PlayerRole.OFFENSIVE).position.minus(input.car.position)).magnitude() < 500) {
            flickController.setupAndUpdateOutput(input);
        }*/
        if(input.car.position.minus(input.ball.position).magnitude() > 1000) {
            output().boost(true);
        }
        //driveToPredictedBallBounceController.setDestination(destination);
        //driveToPredictedBallBounceController.updateOutput(input);

        if (AerialInfo.isBallConsideredAerial(input.ball, input.car.hitBox)) {
            aerialSetupController.setBallDestination(destination);
            aerialSetupController.updateOutput(input);
        } else if (!offensivePlayer.hasWheelContact) {
            aerialRecoveryController.setDestination(new Vector3(offensivePlayer.velocity.flatten(), 0));
            aerialRecoveryController.setRollOrientation(new Vector3(0, 0, 10000));
            aerialRecoveryController.updateOutput(input);
        }
    }

    private void processInputOfBackerPlayer(DataPacket input, GameTickPacket packet) {
        Vector3 playerGoalPosition = new Vector3(0, 5200 * ((input.team*2)-1), 500);
        Vector3 destination = input.ball.position.minus(playerGoalPosition).scaled(0.75).plus(playerGoalPosition);
        driveToDestination2ControllerForBacker.setDestination(destination);
        driveToDestination2ControllerForBacker.setSpeed(input.car.position.minus(destination).magnitude());
        driveToDestination2ControllerForBacker.updateOutput(input);
    }

    private void processInputOfLastManPlayer(DataPacket input, GameTickPacket packet) {
        Vector3 playerGoalPosition = new Vector3(0, 5200 * ((input.team*2)-1), 500);
        Vector3 destination = backerPlayer.position.minus(playerGoalPosition).scaled(0.25).plus(playerGoalPosition);
        driveToDestination2ControllerForLastMan.setDestination(destination);
        driveToDestination2ControllerForLastMan.setSpeed(input.car.position.minus(destination).magnitude());
        driveToDestination2ControllerForLastMan.updateOutput(input);
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        /*super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.blue, lastManPlayer.position.toFlatVector(), backerPlayer.position.toFlatVector());
        renderer.drawLine3d(Color.yellow, backerPlayer.position.toFlatVector(), offensivePlayer.position.toFlatVector());
        renderer.drawLine3d(Color.green, offensivePlayer.position.toFlatVector(), input.ball.position.toFlatVector());*/
    }
}
