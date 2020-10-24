package rlbotexample.bot_behaviour.panbot.test.possession_handling.possession_2v2;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.metagame.advanced_gamestate_info.AerialInfo;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRole;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRoleHandler2V2;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.setup.AerialSetupController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationHandler;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DriveToDestination;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DriveToPredictedBallBounceController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class TeamPossession2v2Test extends PanBot {

    private PlayerRoleHandler2V2 playerRoleHandler2V2;
    private ExtendedCarData offensivePlayer;
    private ExtendedCarData lastManPlayer;
    private AerialSetupController2 aerialSetupController;
    private AerialOrientationHandler aerialRecoveryController;

    private DriveToPredictedBallBounceController driveToPredictedBallBounceController;
    private DriveToDestination driveToDestination2Controller;

    public TeamPossession2v2Test() {
        driveToPredictedBallBounceController = new DriveToPredictedBallBounceController(this);
        driveToDestination2Controller = new DriveToDestination(this);
        aerialSetupController = new AerialSetupController2(this);
        aerialRecoveryController = new AerialOrientationHandler(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        playerRoleHandler2V2 = new PlayerRoleHandler2V2(input);
        offensivePlayer = playerRoleHandler2V2.getPlayerFromRole(PlayerRole.OFFENSIVE);
        lastManPlayer = playerRoleHandler2V2.getPlayerFromRole(PlayerRole.LAST_MAN);

        if(offensivePlayer.playerIndex == input.playerIndex) {
            processInputOfOffensivePlayer(input, packet);
        }
        else {
            processInputOfLastManPlayer(input, packet);
        }

        // return the calculated bot output
        return super.output();
    }

    private void processInputOfOffensivePlayer(DataPacket input, GameTickPacket packet) {
        Vector3 destination = new Vector3(0, -5200 * ((input.team*2)-1), 500);
        driveToPredictedBallBounceController.setDestination(destination);
        driveToPredictedBallBounceController.updateOutput(input);

        if (AerialInfo.isPlayerAllowedToAerial(input.playerIndex, input)) {
            aerialSetupController.setBallDestination(destination);
            aerialSetupController.updateOutput(input);
        } else if (!offensivePlayer.hasWheelContact) {
            aerialRecoveryController.setDestination(new Vector3(offensivePlayer.velocity.flatten(), 0));
            aerialRecoveryController.setRollOrientation(new Vector3(0, 0, 10000));
            aerialRecoveryController.updateOutput(input);
        }
    }

    private void processInputOfLastManPlayer(DataPacket input, GameTickPacket packet) {
        Vector3 playerGoalPosition = new Vector3(0, 5200 * ((input.team*2)-1), 500);
        Vector3 destination = input.ballPrediction.ballAtTime(input.ball.velocity.magnitude()/6000).position.minus(playerGoalPosition).scaled(0.75).plus(playerGoalPosition);
        driveToDestination2Controller.setDestination(destination);
        driveToDestination2Controller.setSpeed(input.car.position.minus(destination).magnitude());
        driveToDestination2Controller.updateOutput(input);
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.green, offensivePlayer.position, input.ball.position);
        renderer.drawLine3d(Color.blue, lastManPlayer.position, offensivePlayer.position);
    }
}
