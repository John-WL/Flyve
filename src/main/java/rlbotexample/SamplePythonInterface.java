package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.boost_pad_utils.BoostPadDijkstraPathFinderTest;
import rlbotexample.bot_behaviour.flyve.debug.boost_pad_utils.BoostPadNodeTest;
import rlbotexample.bot_behaviour.flyve.debug.boost_pad_utils.BoostPadTimerTest;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.NavigateOnPadsTest;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.SimpleBounceDriveTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new NavigateOnPadsTest());
    }
}
