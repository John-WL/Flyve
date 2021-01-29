package rlbotexample;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;
import rlbotexample.bot_behaviour.flyve.debug.ball_prediction.DebugCustomBallPrediction;
import rlbotexample.bot_behaviour.flyve.debug.ball_prediction.DebugPlayerDistanceFromMap;
import rlbotexample.bot_behaviour.flyve.debug.player_values.AngularSpinLogger;
import rlbotexample.bot_behaviour.flyve.debug.player_values.DebugPlayerHitBox;
import rlbotexample.bot_behaviour.flyve.debug.rl_utils.AlgorithmOfRotatorForOrientations;
import rlbotexample.bot_behaviour.flyve.debug.rl_utils.Circle2DIntersections;
import rlbotexample.bot_behaviour.flyve.debug.rl_utils.ExperimentalCurlingTrajectory3DDisplay;
import rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.Normal1sV4;
import rlbotexample.bot_behaviour.flyve.implementation.state_machine_freestyle.DribbleThenJumpAndAerialBot;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.directionnal_hit.AerialDirectionalHit5Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble.AirDribble2Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.aerial.dribble.AirDribble3Test;
import rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.*;
import rlbotexample.bot_behaviour.skill_controller.test.elementary.aerial_orientation.*;

public class SamplePythonInterface extends SocketServer {

    public SamplePythonInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new SampleBot(index, new DebugCustomBallPrediction());
    }
}
