package rlbotexample.bot_behaviour.skill_controller.jump.implementations;

import rlbotexample.bot_behaviour.skill_controller.jump.JumpType;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

public class FishDash extends JumpType {

    private static final int JUMP_DURATION = 1;
    private static final int[] JUMP_TIME_FRAMES = {};

    public FishDash() {
        super(JUMP_DURATION, JUMP_TIME_FRAMES);
    }

    @Override
    public void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation, Vector3 desiredRoofOrientation) {
        updateCurrentJumpCallCounter();
        setJumpState(getCurrentJumpCallCounter() <= JUMP_DURATION);

        if(!this.isJumpFinished()) {
            output.drift(true);
        }
    }
}
