package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.player_values.*;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble.AirDribble2Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.BounceDribble2Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.BounceDribbleTest;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.GroundDirectionalHit4Test;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation.AerialOrientation4Test;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation.AerialOrientation5Test;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation.AerialOrientation7Test;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation.SpinControllerTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new BounceDribble2Test());
    }
}
