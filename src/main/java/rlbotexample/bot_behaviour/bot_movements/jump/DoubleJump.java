package rlbotexample.bot_behaviour.bot_movements.jump;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import rlbotexample.output.ControlsOutput;
import util.vector.Vector3;

public class DoubleJump extends JumpType {

    private static final int JUMP_DURATION = 60;
    private static final int[] JUMP_TIME_FRAMES = {5};

    public DoubleJump() {
        super(JUMP_DURATION, JUMP_TIME_FRAMES);
    }

    @Override
    void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation, Vector3 desiredRoofOrientation) {
        updateCurrentJumpCallCounter();
        setJumpState(getCurrentJumpCallCounter() <= JUMP_DURATION);

        if(!this.getLastJumpState() && !this.isJumpFinished()) {
            output.pitch(0);
            output.yaw(0);
            output.roll(0);
        }

        if(this.getCurrentJumpCallCounter() + 1 == JUMP_TIME_FRAMES[0]) {
            // send a "no-jump" so we can jump a second time the next frame
            setJumpState(false);
        }
    }
}
