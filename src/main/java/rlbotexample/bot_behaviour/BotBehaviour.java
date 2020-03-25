package rlbotexample.bot_behaviour;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public abstract class BotBehaviour {

    private BotOutput myBotOutput;

    public BotBehaviour() {
        myBotOutput = new BotOutput();
    }

    public BotOutput output() {
        return myBotOutput;
    }

    public abstract BotOutput processInput(DataPacket input, GameTickPacket packet);

    public abstract void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime);
}
