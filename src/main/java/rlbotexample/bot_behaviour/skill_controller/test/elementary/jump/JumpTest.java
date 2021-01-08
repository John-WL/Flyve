package rlbotexample.bot_behaviour.skill_controller.test.elementary.jump;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.FlyveBot;
import rlbotexample.bot_behaviour.panbot.debug.player_prediction.DebugPlayerPredictedTrajectory;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.*;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class JumpTest extends FlyveBot {

    private JumpController jumpController;

    public JumpTest() {
        jumpController = new JumpController(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // do the thing

        //if(System.currentTimeMillis() % 1600 < 800) {
            jumpController.setFirstJumpType(new SimpleJump(), input);
            jumpController.setSecondJumpType(new Stall(), input);
            jumpController.setJumpDestination(new Vector3());
            jumpController.updateOutput(input);
            output().drift(true);
        //}

        //System.out.println("First Jump: " + input.allCars.get(1-input.car.playerIndex).hasFirstJump);
        //System.out.println("Second Jump: " + input.allCars.get(1-input.car.playerIndex).hasSecondJump);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        jumpController.debug(renderer, input);
        new DebugPlayerPredictedTrajectory().updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
