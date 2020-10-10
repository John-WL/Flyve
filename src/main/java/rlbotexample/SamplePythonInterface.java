package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.panbot.Normal1sV3;
import rlbotexample.bot_behaviour.panbot.test.aerial.*;
import rlbotexample.bot_behaviour.panbot.test.ground.Dribble2Test;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) { return new SampleBot(index, new Dribble2Test());
    }
}
