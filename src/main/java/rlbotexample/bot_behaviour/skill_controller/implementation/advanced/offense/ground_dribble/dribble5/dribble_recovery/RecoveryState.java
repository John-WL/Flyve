package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.dribble_recovery;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.Dribble5;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.dribble_recovery.recovery_nested_states.CatchBouncyBallState;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.DribbleUtils;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class RecoveryState implements State {

    private final BotBehaviour bot;
    private final Dribble5 masterDribbleController;
    private final StateMachine recoveryMachine;

    public RecoveryState(BotBehaviour bot, Dribble5 masterDribbleController) {
        this.bot = bot;
        this.masterDribbleController = masterDribbleController;

        State initialRecoveryState = new CatchBouncyBallState(bot, masterDribbleController);
        this.recoveryMachine = new StateMachine(initialRecoveryState);
    }

    @Override
    public void exec(DataPacket input) {
        recoveryMachine.exec(input);
    }

    @Override
    public State next(DataPacket input) {
        if(DribbleUtils.botInControl(input)) {
            bot.output().boost(false);
            return new EffectiveDribbleState(bot, masterDribbleController);
        }

        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        //renderer.drawString3d("dribby recovery", Color.YELLOW, input.car.position, 1, 1);
        recoveryMachine.debug(input, renderer);
    }
}
