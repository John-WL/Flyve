package rlbotexample.bot_behaviour.bot_movements.jump;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import rlbotexample.output.ControlsOutput;
import util.vector.Vector3;

public abstract class JumpType {
    private int jumpDuration;
    private int[] jumpKeyTimeFrames;
    private int currentJumpCallCounter;
    private boolean jumpState;
    private boolean lastJumpState;

    public JumpType(int jumpDuration, int[] keyTimeFrames) {
        this.jumpDuration = jumpDuration;
        this.jumpKeyTimeFrames = keyTimeFrames;
        currentJumpCallCounter = 0;
        lastJumpState = false;
        jumpState = false;
    }

    abstract void jump(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation, Vector3 desiredRoofOrientation);

    boolean getLastJumpState() {
        return lastJumpState;
    }

    boolean getJumpState() {
        return jumpState;
    }

    void setJumpState(boolean jumpState) {
        this.lastJumpState = this.jumpState;
        this.jumpState = jumpState;
    }

    public int getJumpDuration() {
        return jumpDuration;
    }

    int[] getKeyTimeFrames() {
        return jumpKeyTimeFrames;
    }

    void updateCurrentJumpCallCounter() {
        currentJumpCallCounter++;
    }

    int getCurrentJumpCallCounter() {
        return currentJumpCallCounter;
    }

    boolean isJumpFinished() {
        return currentJumpCallCounter >= jumpDuration;
    }
}
