package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.flips_and_flicks;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.dribble5.Dribble5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Flip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;

public class Flick2 extends SkillController {

    private static final double PLAYER_DISTANCE_FROM_BALL_WHEN_CONSIDERED_FLICKING = 130;

    private BotBehaviour bot;
    private Dribble5 dribbleController;
    private JumpController jumpController;
    private Vector3 ballDestination;
    private double ballSpeed;
    private boolean isFlicking;

    public Flick2(BotBehaviour bot) {
        this.bot = bot;
        this.dribbleController = new Dribble5(bot);
        this.jumpController = new JumpController(bot);
        this.ballDestination = new Vector3();
        this.ballSpeed = 1200;
        this.isFlicking = false;
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    public void setBallSpeed(double speed) {
        this.ballSpeed = speed;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 ballPosition = input.ball.position;
        Vector3 localBallPosition = ballPosition.minus(input.car.position).toFrameOfReference(input.car.orientation);

        // if the bot can flick
        if(Math.abs(localBallPosition.z) < 180
                && Math.abs(localBallPosition.x) < 90
                && Math.abs(localBallPosition.y) < 90
                && !isFlicking) {
            // flick
            isFlicking = true;
            System.out.println("flick");
        }
        else {
            isFlicking = false;
        }

        if(isFlicking) {
            jumpController.setJumpDestination(ballDestination);
            jumpController.setFirstJumpType(new SimpleJump(), input);
            jumpController.setSecondJumpType(new Flip(), input);
            jumpController.updateOutput(input);
        }
        else {
            // try to dribble so we can flick afterwards
            bot.output().jump(false);
            //dribbleController.setBallDestination(ballDestination);
            //dribbleController.setBallSpeed(ballSpeed);
            dribbleController.updateOutput(input);
        }
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        dribbleController.debug(renderer, input);
    }
}
