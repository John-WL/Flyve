package rlbotexample.bot_behaviour.bot_movements.jump;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import rlbotexample.output.ControlsOutput;
import util.vector.Vector3;

public class FishDash extends JumpType {

    private static final int JUMP_DURATION = 1;
    private static final int[] JUMP_TIME_FRAMES = {};

    public FishDash() {
        super(JUMP_DURATION, JUMP_TIME_FRAMES);
    }

    @Override
    void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation, Vector3 desiredRoofOrientation) {
        updateCurrentJumpCallCounter();
        setJumpState(getCurrentJumpCallCounter() <= JUMP_DURATION);

        if(!this.isJumpFinished()) {
            output.drift(true);
        }
    }
}
