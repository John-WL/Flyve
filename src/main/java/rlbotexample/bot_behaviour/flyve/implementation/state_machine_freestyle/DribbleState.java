package rlbotexample.bot_behaviour.flyve.implementation.state_machine_freestyle;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.Dribble6;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.state_machine.State;

public class DribbleState implements State {

    private BotBehaviour bot;

    private final Dribble6 dribbleController;
    private int dribblingCounter;

    public DribbleState(BotBehaviour bot) {
        this.bot = bot;
        this.dribbleController = new Dribble6(bot);
        this.dribblingCounter = 0;
    }

    @Override
    public void exec(DataPacket input) {
        dribbleController.setBallDestination(new Vector3(0, -5200, 100));
        dribbleController.setTargetSpeed(1200);
        dribbleController.updateOutput(input);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        dribbleController.debug(renderer, input);
    }

    @Override
    public State next(DataPacket input) {
        if(input.ball.position.minus(input.car.position).z < 200
                && input.car.position.flatten().minus(input.ball.position.flatten()).magnitude() < 80
                && input.car.velocity.minus(input.ball.velocity).magnitude() < 100
                && input.car.position.minus(new Vector3(0, -5200, 100)).magnitude() < 3000) {
            dribblingCounter++;
        }
        else {
            dribblingCounter = 0;
        }

        if(dribblingCounter*RlConstants.BOT_REFRESH_TIME_PERIOD > 0.1) {
            dribblingCounter = 0;
            return new DoubleJumpState(bot);
        }

        return this;
    }
}
