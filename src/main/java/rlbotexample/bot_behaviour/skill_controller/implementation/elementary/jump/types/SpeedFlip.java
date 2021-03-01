package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types;

import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpType;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class SpeedFlip extends JumpType {

    private static final int JUMP_DURATION = 25;
    private static final int[] JUMP_TIME_FRAMES = {1, 3, 28};

    private Vector3 savedDesiredFrontOrientation;

    public SpeedFlip() {
        super(JUMP_DURATION);
        savedDesiredFrontOrientation = new Vector3();
    }

    @Override
    public void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation) {
        updateCurrentJumpCallCounter();

        if(this.getCurrentJumpCallCounter() == JUMP_TIME_FRAMES[0]) {
            savedDesiredFrontOrientation = desiredFrontOrientation.normalized();
            output.pitch(-1);
            output.yaw(savedDesiredFrontOrientation.y > 0 ? 1:-1);
            output.roll(0);
            output.jump(true);
        }
        if(this.getCurrentJumpCallCounter() >= JUMP_TIME_FRAMES[1]
                && this.getCurrentJumpCallCounter() <= JUMP_TIME_FRAMES[2]) {
            output.pitch(1);
            output.yaw(0);
            output.roll(savedDesiredFrontOrientation.y > 0 ? 1:-1);
        }
    }
}
