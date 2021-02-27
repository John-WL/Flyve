package rlbotexample.bot_behaviour.flyve.debug.boost_pad_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.boost.BoostManager;
import rlbotexample.input.dynamic_data.boost.BoostPad;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

import java.awt.*;

public class BoostPadTimerTest extends FlyveBot {

    public BoostPadTimerTest() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        for(BoostPad pad: BoostManager.boostPads) {
            renderer.drawString3d(Double.toString(pad.timeBeforeReloaded), Color.YELLOW, pad.location.toFlatVector(), 1, 1);
        }
    }
}
