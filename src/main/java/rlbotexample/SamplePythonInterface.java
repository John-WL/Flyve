package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.panbot.debug.player_values.DebugPlayerSpeedAndAcceleration;
import rlbotexample.bot_behaviour.panbot.debug.player_values.MaxTurnRadiusPrinter;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.GroundDirectionalHitTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new MaxTurnRadiusPrinter());
    }
}
