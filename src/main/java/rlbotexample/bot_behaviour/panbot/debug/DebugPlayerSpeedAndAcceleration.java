package rlbotexample.bot_behaviour.panbot.debug;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.debuging.OtherPlayerAccelerationSpeedPrinter;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;

public class DebugPlayerSpeedAndAcceleration extends PanBot {

    private SkillController skillController;

    public DebugPlayerSpeedAndAcceleration() {
        skillController = new OtherPlayerAccelerationSpeedPrinter();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        skillController.updateOutput(input);
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
