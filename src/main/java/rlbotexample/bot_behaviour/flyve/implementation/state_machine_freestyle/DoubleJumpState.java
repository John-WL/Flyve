package rlbotexample.bot_behaviour.flyve.implementation.state_machine_freestyle;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Flip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.MiddleJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;
import util.state_machine.State;

public class DoubleJumpState implements State {
    BotBehaviour bot;

    private final JumpController jumpController;

    public DoubleJumpState(BotBehaviour bot) {
        this.bot = bot;
        this.jumpController = new JumpController(bot);
    }

    @Override
    public void exec(DataPacket input) {
        jumpController.setFirstJumpType(new ShortJump(), input);
        jumpController.setSecondJumpType(new ShortJump(), input);
        jumpController.setJumpDestination(new Vector3(0, -5200, 100));
        jumpController.updateOutput(input);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {

    }

    @Override
    public State next(DataPacket input) {
        if(input.car.hasUsedSecondJump) {
            return new AerialHitState(bot);
        }

        return this;
    }
}
