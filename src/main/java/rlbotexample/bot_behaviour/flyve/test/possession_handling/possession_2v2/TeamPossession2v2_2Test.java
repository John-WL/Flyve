package rlbotexample.bot_behaviour.flyve.test.possession_handling.possession_2v2;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRole;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRoleHandler2V2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.path_follower.GroundTrajectoryFollower;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.Dribble6;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.boost.BoostManager;
import rlbotexample.input.dynamic_data.boost.BoostPad;
import rlbotexample.input.dynamic_data.boost.BoostPadNavigation;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.output.BotOutput;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class TeamPossession2v2_2Test extends FlyveBot {

    private PlayerRoleHandler2V2 playerRoleHandler2V2;
    private ExtendedCarData offensivePlayer;
    private ExtendedCarData lastManPlayer;

    private Dribble6 dribbleController;
    private GroundTrajectoryFollower groundTrajectoryFollower;

    public TeamPossession2v2_2Test() {
        this.dribbleController = new Dribble6(this);
        this.groundTrajectoryFollower = new GroundTrajectoryFollower(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing
        playerRoleHandler2V2 = new PlayerRoleHandler2V2(input);
        offensivePlayer = playerRoleHandler2V2.getPlayerFromRole(PlayerRole.OFFENSIVE);
        lastManPlayer = playerRoleHandler2V2.getPlayerFromRole(PlayerRole.LAST_MAN);

        if(offensivePlayer.playerIndex == input.playerIndex) {
            processInputOfOffensivePlayer(input);
        }
        else {
            processInputOfLastManPlayer(input);
        }

        // return the calculated bot output
        return super.output();
    }

    private void processInputOfOffensivePlayer(DataPacket input) {
        dribbleController.setBallDestination(StandardMapGoals.getOpponent(offensivePlayer.team).normal.offset);
        dribbleController.setTargetSpeed(1200);
        dribbleController.updateOutput(input);
    }

    private void processInputOfLastManPlayer(DataPacket input) {
        Optional<BoostPad> nicestPad = BoostManager.closestActivePadFrom(lastManPlayer.position);
        Optional<BoostPad> endPad = BoostManager.closestActivePadFrom(offensivePlayer.position);
        nicestPad.ifPresent(closestPad ->
            endPad.ifPresent(destinationPad -> {
                List<BoostPad> path = BoostPadNavigation.dijkstraPathFinding(closestPad, destinationPad, input);
                groundTrajectoryFollower.pathToFollow = time -> path.get(0).location;
            }));
        groundTrajectoryFollower.boostEnabled(false);
        groundTrajectoryFollower.updateOutput(input);
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        renderer.drawString3d("offensive", Color.YELLOW, offensivePlayer.position.toFlatVector(), 2, 2);
        if(input.car == offensivePlayer) {
            dribbleController.debug(renderer, input);
        }
        renderer.drawString3d("last man", Color.YELLOW, lastManPlayer.position.toFlatVector(), 2, 2);
    }
}
