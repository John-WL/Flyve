package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.dribble_recovery.recovery_nested_states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.Dribble5;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.dribble_recovery.recovery_nested_states.catch_rolling_ball_states.GiveItANudgeState;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.DribbleUtils;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class CatchRollingBallState implements State {

    private final BotBehaviour bot;
    private final Dribble5 masterDribbleController;
    private final StateMachine rollingBallMachine;

    public CatchRollingBallState(BotBehaviour bot, Dribble5 masterDribbleController) {
        this.bot = bot;
        this.masterDribbleController = masterDribbleController;
        State initialCatchRollingBallState = new GiveItANudgeState(bot, masterDribbleController);
        this.rollingBallMachine = new StateMachine(initialCatchRollingBallState);
    }

    @Override
    public void exec(DataPacket input) {
        rollingBallMachine.exec(input);
    }

    @Override
    public State next(DataPacket input) {
        if(DribbleUtils.ballBouncingTooMuchForSwipe(input)) {
            bot.output().boost(false);
            return new CatchBouncyBallState(bot, masterDribbleController);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        //renderer.drawString3d("do the swipe", Color.YELLOW, input.car.position, 1, 1);
        rollingBallMachine.debug(input, renderer);
    }
}
