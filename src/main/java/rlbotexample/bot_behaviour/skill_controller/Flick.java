package rlbotexample.bot_behaviour.skill_controller;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.Dribble;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.Flip;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.ShortJump;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.vector.Vector3;

public class Flick extends SkillController {

    private static final double PLAYER_DISTANCE_FROM_BALL_WHEN_CONSIDERED_FLICKING = 130;

    private BotBehaviour bot;
    private Dribble dribbleController;
    private JumpHandler jumpHandler;
    private boolean isFlicking;
    private boolean isLastFlickingFrame;

    public Flick(BotBehaviour bot) {
        this.bot = bot;
        this.dribbleController = new Dribble(bot);
        this.jumpHandler = new JumpHandler();
        this.isFlicking = false;
        this.isLastFlickingFrame = false;
    }

    @Override
    public void updateOutput(DataPacket input) {
        // get useful values
        Vector3 playerPosition = input.car.position;
        Vector3 playerNoseOrientation = input.car.orientation.noseVector;
        Vector3 ballPosition = input.ball.position;
        //Vector3 nonUniformScaledPlayerDistanceFromBall = ((aerialKinematicBody.minus(getNativeBallPrediction)).minusAngle(playerNoseOrientation)).scaled(1, 1, 1);

        /*
        // if the bot can flick
        if(nonUniformScaledPlayerDistanceFromBall.magnitude() < PLAYER_DISTANCE_FROM_BALL_WHEN_CONSIDERED_FLICKING) {
            // flick
            isFlicking = true;
        } */

        // if the bot can flick

    }

    private void updateJumpBehaviour(DataPacket input) {
        // get useful values
        BotOutput output = bot.output();
        Vector3 playerPosition = input.car.position;
        Vector3 myRoofVector = input.car.orientation.roofVector;
        Vector3 ballPosition = input.ball.position;

        if (jumpHandler.isJumpFinished()) {

            if(input.car.hasWheelContact) {
                jumpHandler.setJumpType(new ShortJump());
                isLastFlickingFrame = false;

                // don't rotate before flicking
                output.pitch(0);
                output.yaw(0);
                output.roll(0);

            }
            else {
                jumpHandler.setJumpType(new Flip());
                isLastFlickingFrame = true;
            }
        }

        if (jumpHandler.isJumpFinished()) {
            if (isLastFlickingFrame) {
                isFlicking = false;
            }
        }
        output.jump(jumpHandler.getJumpState());
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        dribbleController.debug(renderer, input);
    }
}
