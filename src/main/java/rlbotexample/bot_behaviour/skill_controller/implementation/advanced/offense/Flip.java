package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.MiddleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class Flip extends SkillController {

    private BotBehaviour bot;
    private Vector3 destination;
    private JumpController jumpHandler;

    public Flip(BotBehaviour bot) {
        this.bot = bot;
        this.destination = new Vector3();
        this.jumpHandler = new JumpController(bot);
    }

    public void setDestination(final Vector3 destination) {
        this.destination = destination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        updateJumpBehaviour(input);
    }

    private void updateJumpBehaviour(DataPacket input) {
        // get useful values
        BotOutput output = bot.output();
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
