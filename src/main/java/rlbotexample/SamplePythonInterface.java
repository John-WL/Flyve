package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.player_ball_interaction.DebugBallHitNormalOnPlayer;
import rlbotexample.bot_behaviour.flyve.debug.player_ball_interaction.DebugBallHitOnCar;
import rlbotexample.bot_behaviour.flyve.debug.player_values.AngularDampeningLogger;
import rlbotexample.bot_behaviour.flyve.debug.player_values.DebugPlayerOctaneWheelBox;
import rlbotexample.bot_behaviour.flyve.debug.player_values.DriftLogger;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit5Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit7Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble.AirDribble2Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.recovery.AerialRecoveryTest;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new AerialRecoveryTest());
    }
}
