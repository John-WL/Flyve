package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.rl_utils.trajectories.ExperimentalCurlingTrajectory3DDisplay;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.CushionBouncyBallTest;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.SimpleBounceDriveTest;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.WallToAirDribbleTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new SimpleBounceDriveTest());
    }
}
