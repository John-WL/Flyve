package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.dribble_recovery.RecoveryState;
import rlbotexample.input.dynamic_data.DataPacket;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class Dribble5 extends SkillController {

    private final BotBehaviour bot;

    private final StateMachine dribbleMachine;

    public double throttleAmount;
    public double steerAmount;

    public Dribble5(BotBehaviour bot) {
        this.bot = bot;
        State initialDribbleState = new RecoveryState(bot, this);
        this.dribbleMachine = new StateMachine(initialDribbleState);

        this.throttleAmount = 0;
        this.steerAmount = 0;
    }

    public void throttle(double throttleAmount) {
        this.throttleAmount = throttleAmount;
    }

    public void steer(double steerAmount) {
        this.steerAmount = steerAmount;
    }

    @Override
    public void updateOutput(DataPacket input) {
        dribbleMachine.exec(input);
    }

    @Override
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        dribbleMachine.debug(input, renderer);
    }
}
