package rlbotexample.bot_behaviour.bot_movements.jump;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

public class Wait extends JumpType {

    private static final int JUMP_DURATION = 1;
    private static final int[] JUMP_TIME_FRAMES = {};

    public Wait() {
        super(JUMP_DURATION, JUMP_TIME_FRAMES);
    }

    @Override
    void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation, Vector3 desiredRoofOrientation) {
        updateCurrentJumpCallCounter();
        setJumpState(false);
    }
}
