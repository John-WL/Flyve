package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.panbot.debug.ball_prediction.DebugCustomBallPrediction;
import rlbotexample.bot_behaviour.panbot.debug.ball_values.DebugBallHeight;
import rlbotexample.bot_behaviour.panbot.debug.player_values.DebugPlayerAverageBoostUsage;
import rlbotexample.bot_behaviour.panbot.debug.rl_utils.ConstantAccelerationToReachAerialDestination;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit3Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit4Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit5Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble.AirDribble2Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.setup.AerialSetupController3Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.setup.AerialSetupController4Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.*;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.jump.JumpTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new AerialSetupController4Test());
    }
}
