package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class AngularSpinLogger extends FlyveBot {

    public AngularSpinLogger() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        System.out.println(input.car.spin.toFrameOfReference(input.car.orientation));

        //output().roll(0.8);
        //output().pitch(0.6);
        output().yaw(0.6);

        return output();
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        renderer.drawLine3d(Color.GREEN, input.car.spin.scaled(100).plus(new Vector3(0, 0, 100)), new Vector3(0, 0, 100));
    }
}
