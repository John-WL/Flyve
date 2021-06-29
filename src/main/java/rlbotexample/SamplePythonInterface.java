package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.ball_prediction.DebugCustomBallPrediction;
import rlbotexample.bot_behaviour.flyve.debug.player_values.DebugPlayerSpeedAndAcceleration;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.Normal1sV1;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.Normal1sV2;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.Normal1sV3;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.Normal1sV4;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble.AirDribble2Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.directionnal_hit.*;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.dribble.Dribble6Test;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new AirDribble2Test());
    }
}
