package rlbotexample.bot_behaviour.panbot;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense.Dribble;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.debug.BezierDebugger;
import util.vector.Vector3;

import java.awt.*;

public class Normal1sV0 extends PanBot {

    private SkillController skillController;

    public Normal1sV0() {
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        // do the thing
        skillController.setupAndUpdateOutputs(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        Vector3 playerPosition = input.car.position;

        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
