package rlbotexample.bot_behaviour.panbot.debug;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class DebugPlayerDistanceFromBall extends PanBot {

    public DebugPlayerDistanceFromBall() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        System.out.println(input.ball.position.minus(input.allCars.get(1-input.playerIndex).position));
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
