package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class AccelerationLogger extends FlyveBot {

    public AccelerationLogger() {

    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        output().throttle(0.5);
        System.out.println(input.car.velocity.magnitude());

        return output();
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
