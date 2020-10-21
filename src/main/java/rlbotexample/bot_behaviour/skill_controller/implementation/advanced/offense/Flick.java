package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Flip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class Flick extends SkillController {

    private static final double PLAYER_DISTANCE_FROM_BALL_WHEN_CONSIDERED_FLICKING = 130;

    private BotBehaviour bot;
    private Dribble2 dribbleController;
    private JumpController jumpHandler;
    private boolean isFlicking;
    private boolean isLastFlickingFrame;

    public Flick(BotBehaviour bot) {
        this.bot = bot;
        this.dribbleController = new Dribble2(bot);
        this.jumpHandler = new JumpController(bot);
        this.isFlicking = false;
        this.isLastFlickingFrame = false;
    }

    @Override
    public void updateOutput(DataPacket input) {
        // get useful values
        Vector3 playerPosition = input.car.position;
        Vector3 playerNoseOrientation = input.car.orientation.noseVector;
        Vector3 ballPosition = input.ball.position;
        Vector3 localBallPosition = ballPosition.minus(input.car.position).toFrameOfReference(input.car.orientation);
        //Vector3 nonUniformScaledPlayerDistanceFromBall = ((aerialKinematicBody.minus(getNativeBallPrediction)).minusAngle(playerNoseOrientation)).scaled(1, 1, 1);

        /*
        // if the bot can flick
        if(nonUniformScaledPlayerDistanceFromBall.magnitude() < PLAYER_DISTANCE_FROM_BALL_WHEN_CONSIDERED_FLICKING) {
            // flick
            isFlicking = true;
        } */

        // if the bot can flick
        if(Math.abs(localBallPosition.z) < 160
                && Math.abs(localBallPosition.x) < 120
                && Math.abs(localBallPosition.y) < 155
                && !isFlicking) {
            // flick
            isFlicking = true;
            //System.out.println("flick");
        }

        if(isFlicking) {
            updateJumpBehaviour(input);
        }
        else {
            // try to dribble so we can flick afterwards
            dribbleController.updateOutput(input);
        }
    }

    private void updateJumpBehaviour(DataPacket input) {
        // get useful values
        BotOutput output = bot.output();
        Vector3 ballPosition = input.ball.position;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        dribbleController.debug(renderer, input);
    }
}
