package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types;

import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpType;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class HalfFlip extends JumpType {

    private static final int JUMP_DURATION = 28;
    private static final int[] JUMP_TIME_FRAMES = {1, 10, 11, 22};

    public HalfFlip() {
        super(JUMP_DURATION);
    }

    @Override
    public void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation) {
        updateCurrentJumpCallCounter();

        if(this.getCurrentJumpCallCounter() == JUMP_TIME_FRAMES[0]) {
            output.pitch(1);
            output.yaw(0);
            output.roll(0);
            output.jump(true);
        }
        if(this.getCurrentJumpCallCounter() > JUMP_TIME_FRAMES[1]
                && this.getCurrentJumpCallCounter() <= JUMP_TIME_FRAMES[2]) {
            output.pitch(-1);
            output.yaw(0);
            output.roll(0);
        }
        if(this.getCurrentJumpCallCounter() > JUMP_TIME_FRAMES[2]
                && this.getCurrentJumpCallCounter() <= JUMP_TIME_FRAMES[3]) {
            output.pitch(-1);
            output.yaw(0);
            output.roll(-1);
        }
        if(this.getCurrentJumpCallCounter() > JUMP_TIME_FRAMES[3]) {
            output.pitch(0);
            output.yaw(0);
            output.roll(-1);
        }
    }
}
