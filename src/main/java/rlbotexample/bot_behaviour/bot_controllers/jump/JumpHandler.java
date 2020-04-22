package rlbotexample.bot_behaviour.bot_controllers.jump;

import rlbotexample.bot_behaviour.bot_controllers.jump.implementations.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

public class JumpHandler {

    private static final int NUMBER_OF_FRAMES_AT_30_FPS_BEFORE_LOOSING_2ND_JUMP = 44;

    private int flipCounter;
    private boolean firstJumpOccurred;
    private JumpType jumpType;

    public JumpHandler() {
        firstJumpOccurred = false;
        jumpType = new Wait();
    }

    public void updateJumpState(DataPacket input, BotOutput output, Vector3 desiredFrontOrientation, Vector3 desiredRoofOrientation) {
        if(input.car.hasWheelContact) {
            firstJumpOccurred = false;
            flipCounter = 0;
        }
        else if(firstJumpOccurred) {
            flipCounter++;
        }

        // update the jump variable in the current jumpType
        jumpType.jump(input, output, desiredFrontOrientation, desiredRoofOrientation);
        if(jumpType.getJumpState()) {
            if(!firstJumpOccurred) {
                // If the first jump is from the ceiling, then it hasn't happened at all,
                // but the second has, and so that's why the first must have happened at some point.
                // This is just a convention though. We could have let the first jump un-occurred,
                // but it's practical this way.
                firstJumpOccurred = true;
            }
            else {
                // notify we can't jump anymore (see "hasSecondJump()" function)
                flipCounter = NUMBER_OF_FRAMES_AT_30_FPS_BEFORE_LOOSING_2ND_JUMP;
            }
        }


        /*
        switch(jumpType) {
            case WAVE_DASH:
                // I'M IMPLEMENTING IT IN THE FUTURE BUT NOT FOR NOW
                // it's a little bit too advanced for my needs at the moment.
                break;
        }*/
    }

    public boolean getJumpState() {
        return jumpType.getJumpState();
    }

    public void setJumpType(JumpType jumpType) {
        this.jumpType = jumpType;
    }

    public boolean isJumpFinished() {
        return jumpType.isJumpFinished();
    }

    boolean hasFirstJumpOccured() {
        return firstJumpOccurred;
    }

    public boolean hasSecondJump() {
        return flipCounter < NUMBER_OF_FRAMES_AT_30_FPS_BEFORE_LOOSING_2ND_JUMP;
    }

    public int timeBeforeLoosingSecondJump() {
        return NUMBER_OF_FRAMES_AT_30_FPS_BEFORE_LOOSING_2ND_JUMP - flipCounter;
    }
}
