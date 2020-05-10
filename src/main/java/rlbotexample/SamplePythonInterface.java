package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.panbot.test.AerialDirectionalHitPredictionTest;
import rlbotexample.bot_behaviour.panbot.test.AerialHitPredictionTest;
import rlbotexample.bot_behaviour.panbot.test.AerialIntersectDestinationTest;
import rlbotexample.bot_behaviour.panbot.test.AerialPassingPlayTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new AerialPassingPlayTest());
    }
}
