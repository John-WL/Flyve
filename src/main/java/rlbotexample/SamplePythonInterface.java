package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.player_values.AngularAccelerationLogger;
import rlbotexample.bot_behaviour.flyve.implementation.ml.MlCopyMovements;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.*;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new MlCopyMovements());
    }
}
