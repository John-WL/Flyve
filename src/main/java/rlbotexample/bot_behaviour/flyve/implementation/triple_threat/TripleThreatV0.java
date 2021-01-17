package rlbotexample.bot_behaviour.flyve.implementation.triple_threat;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRole;
import rlbotexample.bot_behaviour.metagame.possessions.PlayerRoleHandler3V3;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class TripleThreatV0 extends FlyveBot {

    private KickoffBehaviour kickoffBehaviour;
    private boolean isKickoffReset;

    private OffensiveBehaviour offensiveBehaviour;
    private BackerBehaviour backerBehaviour;
    private LastManBehaviour lastManBehaviour;

    public TripleThreatV0() {
        this.kickoffBehaviour = new KickoffBehaviour();
        this.isKickoffReset = true;

        this.offensiveBehaviour = new OffensiveBehaviour();
        this.backerBehaviour = new BackerBehaviour();
        this.lastManBehaviour = new LastManBehaviour();
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        final PlayerRoleHandler3V3 playerRoleHandler = new PlayerRoleHandler3V3(input, input.team);

        if(isKickoffState(input)) {
            resetKickoffBehaviourIfNeeded();
            setOutput(kickoffBehaviour.processInput(input, packet));
        }
        else {
            isKickoffReset = false;
            handleMatch(playerRoleHandler, input, packet);
        }

        return super.output();
    }

    public boolean isKickoffState(DataPacket input) {
        return input.ball.velocity.magnitude() < 0.1;
    }

    public void resetKickoffBehaviourIfNeeded() {
        if(!isKickoffReset) {
            kickoffBehaviour = new KickoffBehaviour();
            isKickoffReset = true;
        }
    }

    public void handleMatch(PlayerRoleHandler3V3 playerRoleHandler, DataPacket input, GameTickPacket packet) {
        PlayerRole playerRole = playerRoleHandler.getPlayerRole(input);
        if(playerRole == PlayerRole.OFFENSIVE) {
            setOutput(offensiveBehaviour.processInput(input, packet));
        }
        else if(playerRole == PlayerRole.BACKER) {
            setOutput(backerBehaviour.processInput(input, packet));
        }
        else {
            setOutput(lastManBehaviour.processInput(input, packet));
        }
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
