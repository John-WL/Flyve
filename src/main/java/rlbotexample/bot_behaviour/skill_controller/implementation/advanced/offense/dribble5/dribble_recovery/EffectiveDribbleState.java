package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.dribble_recovery;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Dribble4;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.Dribble5;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.DribbleUtils;
import util.state_machine.State;

import java.awt.*;

public class EffectiveDribbleState implements State {

    private final BotBehaviour bot;
    private final Dribble4 dribbleController;
    private final Dribble5 masterDribbleController;

    public EffectiveDribbleState(BotBehaviour bot, Dribble5 masterDribbleController) {
        this.bot = bot;
        this.dribbleController = new Dribble4(bot);
        this.masterDribbleController = masterDribbleController;
    }

    @Override
    public void exec(DataPacket input) {
        double throttleAmount = masterDribbleController.throttleAmount;
        double steerAmount = masterDribbleController.steerAmount;
        dribbleController.throttle(throttleAmount);
        dribbleController.steer(steerAmount);
        dribbleController.updateOutput(input);
    }

    @Override
    public State next(DataPacket input) {
        if(!DribbleUtils.botInControl(input)) {
            bot.output().boost(false);
            return new RecoveryState(bot, masterDribbleController);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("effective dribby", Color.YELLOW, input.car.position, 1, 1);
    }
}
