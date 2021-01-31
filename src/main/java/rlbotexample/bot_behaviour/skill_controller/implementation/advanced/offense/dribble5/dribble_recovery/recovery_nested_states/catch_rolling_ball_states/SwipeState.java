package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.dribble_recovery.recovery_nested_states.catch_rolling_ball_states;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.Dribble5;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;

import java.awt.*;

public class SwipeState implements State {

    private final BotBehaviour bot;
    private final Dribble5 masterDribbleController;

    public SwipeState(BotBehaviour bot, Dribble5 masterDribbleController) {
        this.bot = bot;
        this.masterDribbleController = masterDribbleController;
    }

    @Override
    public void exec(DataPacket input) {

    }

    @Override
    public State next(DataPacket input) {
        if(input.ball.velocity
                .dotProduct(input.car.position.minus(input.ball.position)
                        .normalized()) >= 0) {
            bot.output().boost(false);
            return new GiveItANudgeState(bot, masterDribbleController);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("swipe not wipe", Color.YELLOW, input.car.position, 1, 1);
    }
}
