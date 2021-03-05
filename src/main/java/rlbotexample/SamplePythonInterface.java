package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.ball_prediction.DebugCustomBallPrediction;
import rlbotexample.bot_behaviour.flyve.debug.player_values.AccelerationLogger;
import rlbotexample.bot_behaviour.flyve.debug.player_values.DebugPlayerSpeedAndAcceleration;
import rlbotexample.bot_behaviour.flyve.debug.player_values.MaxTurnRadiusPrinter;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit5Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.directionnal_hit.GroundDirectionalHit4Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.directionnal_hit.GroundDirectionalHit5Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.directionnal_hit.GroundDirectionalHit6Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.weak_dribble.WeakDribble6Test;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving.DrivingAccelerationControllerTest;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving.DrivingSpeedController2Test;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.general_driving.GroundSpinControllerTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new AerialDirectionalHit5Test());
    }
}
