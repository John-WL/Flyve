package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;

public class DebugPlayerSpeedMinusBallSpeed extends FlyveBot {

    Vector3 deltaSpeed;

    public DebugPlayerSpeedMinusBallSpeed() {
        deltaSpeed = new Vector3();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        deltaSpeed = input.allCars.get(1-input.playerIndex).velocity.minus(input.ball.velocity);
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        renderer.drawString3d("" + deltaSpeed.magnitude(), Color.YELLOW, input.ball.position, 2, 2);
        renderer.drawLine3d(Color.CYAN, input.allCars.get(1-input.playerIndex).position, input.ball.position);
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
