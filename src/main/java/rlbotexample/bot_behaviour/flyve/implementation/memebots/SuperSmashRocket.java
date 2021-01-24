package rlbotexample.bot_behaviour.flyve.implementation.memebots;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.flyve.implementation.memebots.alternative_physics.GuiOfSsr;
import rlbotexample.bot_behaviour.flyve.implementation.memebots.alternative_physics.PhysicsOfSsr;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class SuperSmashRocket extends FlyveBot {

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        PhysicsOfSsr.execute(input);

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        GuiOfSsr.print(input, renderer);
    }
}
