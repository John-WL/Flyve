package rlbotexample.bot_behaviour.panbot.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.debug.OtherPlayerAccelerationSpeedPrinter;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class DebugPlayerAverageBoostUsage extends PanBot {
    public DebugPlayerAverageBoostUsage() {

    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        System.out.println(input.allCars.get(1-input.playerIndex).averageBoostUsage);
        //System.out.println(input.car.previousBoost);

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}