package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.panbot.debug.player_values.DebugPlayerSpeedAndAcceleration;
import rlbotexample.bot_behaviour.panbot.debug.player_values.MaxTurnRadiusPrinter;
import rlbotexample.bot_behaviour.panbot.debug.rl_utils.*;
import rlbotexample.bot_behaviour.panbot.implementation.normal_1s.Normal1sV3;
import rlbotexample.bot_behaviour.panbot.implementation.normal_1s.normal_1s_v4.Normal1sV4;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola.AerialDirectionalHit6;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.GroundDirectionalHit3;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit5Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit6Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.DemolishTest;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.GroundDirectionalHit2Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.GroundDirectionalHit3Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.GroundDirectionalHitTest;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation.AerialOrientation2Test;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new Circle2DTangentsToOtherCircle());
    }
}
