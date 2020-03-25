package rlbotexample.bot_behaviour.bot_movements.jump;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import rlbotexample.output.ControlsOutput;
import util.vector.Vector3;

public class JumpAndFlip extends JumpType {

    private static final int JUMP_DURATION = 20;
    private static final int[] JUMP_TIME_FRAMES = {2, 7};

    public JumpAndFlip() {
        super(JUMP_DURATION, JUMP_TIME_FRAMES);
    }

    @Override
    void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation, Vector3 desiredRoofOrientation) {
        updateCurrentJumpCallCounter();


        if(this.getCurrentJumpCallCounter() == JUMP_TIME_FRAMES[1]) {
            Vector3 flipDirections = desiredFrontOrientation.normalized();
            output.pitch(-flipDirections.x);
            output.yaw(-flipDirections.y);
            //output.withRoll(desiredRoofOrientation.y);
        }
        if(this.getCurrentJumpCallCounter() > JUMP_TIME_FRAMES[1] && !this.isJumpFinished()) {
            output.pitch(0);
            output.yaw(0);
            output.roll(0);
        }
        if(this.getCurrentJumpCallCounter() + 1 == JUMP_TIME_FRAMES[0] || this.getCurrentJumpCallCounter() + 1 == JUMP_TIME_FRAMES[1]) {
            // send a "no-jump" so we can jump a second time the next frame
            setJumpState(false);
        }
        else {
            setJumpState(getCurrentJumpCallCounter() <= JUMP_DURATION);
        }
    }
}
