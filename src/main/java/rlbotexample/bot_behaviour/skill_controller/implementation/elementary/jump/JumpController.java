package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;

public class JumpController extends SkillController {

    private static final int NUMBER_OF_FRAMES_AT_30_FPS_BEFORE_LOOSING_2ND_JUMP = 44;

    private BotBehaviour bot;

    private Vector3 jumpDestination;

    private JumpType firstJumpType;
    private JumpType secondJumpType;

    public JumpController(BotBehaviour bot) {
        this.bot = bot;
        this.jumpDestination = new Vector3();

        this.firstJumpType = new Wait();
        this.secondJumpType = new Wait();
    }

    public void setFirstJumpType(JumpType jumpType, DataPacket input) {
        if(hasFirstJump(input) && firstJumpType.canBeReloaded()) {
            this.firstJumpType = jumpType;
        }
    }

    public void setSecondJumpType(JumpType jumpType, DataPacket input) {
        if(hasSecondJump(input) && secondJumpType.canBeReloaded()) {
            this.secondJumpType = jumpType;
        }
    }

    public void setJumpDestination(Vector3 jumpDestination) {
        this.jumpDestination = jumpDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        performJump(input);
    }

    private void performJump(DataPacket input) {
        JumpType jumpType;

        if(!firstJumpType.isJumpFinished()) {
            jumpType = firstJumpType;
        }
        else if(!secondJumpType.isJumpFinished()) {
            jumpType = secondJumpType;
        }
        else {
            jumpType = new Wait();
        }
        bot.output().jump(false);
        jumpType.jump(input, bot.output(), jumpDestination.minus(input.car.position).normalized().orderedMinusAngle(input.car.orientation.noseVector.scaled(-1)));
    }

    private boolean hasFirstJump(DataPacket input) {
        return input.car.hasFirstJump;
    }

    private boolean hasSecondJump(DataPacket input) {
        return input.car.hasSecondJump;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
    }
}
