package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types;

import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpType;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class SpeedFlip extends JumpType {

    private static final int JUMP_DURATION = 20;
    private static final int[] JUMP_TIME_FRAMES = {1, 2, 20};

    public SpeedFlip() {
        super(JUMP_DURATION);
    }

    @Override
    public void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation) {
        updateCurrentJumpCallCounter();

        if(this.getCurrentJumpCallCounter() == JUMP_TIME_FRAMES[0]) {
            output.pitch(-0.707);
            output.yaw(0.707);
            output.roll(0);
            output.jump(true);
        }
        if(this.getCurrentJumpCallCounter() > JUMP_TIME_FRAMES[1]
                && this.getCurrentJumpCallCounter() <= JUMP_TIME_FRAMES[2]) {
            output.pitch(0.707);
            output.yaw(0.707);
            output.roll(0);
        }
    }
}
