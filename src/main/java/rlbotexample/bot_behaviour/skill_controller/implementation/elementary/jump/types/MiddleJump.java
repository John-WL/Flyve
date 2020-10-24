package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types;

import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpType;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class MiddleJump extends JumpType {

    private static final int JUMP_DURATION = 4;
    private static final int[] JUMP_TIME_FRAMES = {4};

    public MiddleJump() {
        super(JUMP_DURATION);
    }

    @Override
    public void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation) {
        updateCurrentJumpCallCounter();

        if(this.getCurrentJumpCallCounter() < JUMP_TIME_FRAMES[0]) {
            output.pitch(0);
            output.yaw(0);
            output.roll(0);
            output.jump(true);
        }
    }
}
