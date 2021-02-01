package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.CushionBouncyBall;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.SimpleBounceDrive;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

public class SimpleBounceDriveTest extends FlyveBot {

    public SimpleBounceDrive simpleBounceDrive;

    public SimpleBounceDriveTest() {
        this.simpleBounceDrive = new SimpleBounceDrive(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        simpleBounceDrive.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        simpleBounceDrive.debug(renderer, input);
    }
}
