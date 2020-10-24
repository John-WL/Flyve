package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types;

import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpType;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class Flip extends JumpType {

    private static final int JUMP_DURATION = 20;
    private static final int[] JUMP_TIME_FRAMES = {1, 20};

    public Flip() {
        super(JUMP_DURATION);
    }

    @Override
    public void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation) {
        updateCurrentJumpCallCounter();

        if(this.getCurrentJumpCallCounter() == JUMP_TIME_FRAMES[0]) {
            Vector2 flipDirections = desiredFrontOrientation.flatten().normalized();
            output.pitch(flipDirections.x);
            output.yaw(flipDirections.y);
            output.roll(0);
            output.jump(true);
        }
        else if(this.getCurrentJumpCallCounter() > JUMP_TIME_FRAMES[0]
                && this.getCurrentJumpCallCounter() <= JUMP_TIME_FRAMES[1]) {
            output.pitch(0);
            output.yaw(0);
            output.roll(0);
        }
    }
}
