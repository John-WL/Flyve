package rlbotexample.bot_behaviour.skill_controller.advanced_controller;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.AerialOrientationHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.vector.Vector3;

import java.awt.*;

public class AerialDirectionalHit extends SkillController {

    private BotBehaviour bot;
    private Predictions predictions;
    private AerialOrientationHandler aerialOrientationHandler;
    private JumpHandler jumpHandler;
    private Vector3 ballDestination;

    private Vector3 orientation;
    private Vector3 hitPositionOnBall;
    private Vector3 ballFuturePosition;

    public AerialDirectionalHit(BotBehaviour bot, Predictions predictions) {
        this.bot = bot;
        this.predictions = predictions;
        this.aerialOrientationHandler = new AerialOrientationHandler(bot);
        this.jumpHandler = new JumpHandler();
        this.ballDestination = new Vector3();

        this.orientation = new Vector3();
        this.hitPositionOnBall = new Vector3();
        this.ballFuturePosition = new Vector3();
    }

    public void setBallDestination(Vector3 destination) {
        ballDestination = destination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 playerPosition = input.car.position;
        Vector3 playerSpeed = input.car.velocity;
        Vector3 playerDistanceFromBall = input.ball.position.minus(playerPosition);
        Vector3 playerSpeedFromBall = input.ball.velocity.minus(playerSpeed);

        double timeBeforeReachingBall = predictions.timeToReachAerialDestination(playerDistanceFromBall, playerSpeedFromBall);

        // get the future player and getNativeBallPrediction positions
        Vector3 playerFuturePosition = input.ball.position;
        if(input.ball.velocity.magnitude() > 0.1) {
            playerFuturePosition = predictions.aerialKinematicBody(playerPosition, playerSpeed, timeBeforeReachingBall).getPosition();
        }
        Vector3 ballFuturePosition = predictions.resultingBallTrajectoryFromAerialHit(input.car, input.ball, timeBeforeReachingBall).getPosition();

        // get the getNativeBallPrediction offset so we actually hit the getNativeBallPrediction to make it go in the desired direction
        Vector3 ballOffset = ballFuturePosition.minus(ballDestination.plus(new Vector3(0, 0, 1000))).scaledToMagnitude(RlConstants.BALL_RADIUS);

        // get the orientation we should have to hit the getNativeBallPrediction
        Vector3 orientation = ballFuturePosition.plus(ballOffset).minus(playerFuturePosition);

        // update variables so we can print them later in the debugger
        hitPositionOnBall = ballFuturePosition.plus(ballOffset);
        this.orientation = orientation;
        this.ballFuturePosition = ballFuturePosition;

        // boost to the destination
        if(input.car.orientation.noseVector.dotProduct(orientation)/orientation.magnitude() > 0.6) {
            output.boost(true);
        }
        else {
            output.boost(false);
        }


        // if we're hitting the getNativeBallPrediction in the future, face the getNativeBallPrediction to hit it properly
        if(hitPositionOnBall.minus(playerFuturePosition).magnitude() < 20) {
            output.boost(false);
            orientation = ballFuturePosition.plus(ballOffset).minus(input.car.position);
            this.orientation = orientation;
        }

        // set the desired orientation and apply it
        aerialOrientationHandler.setDestination(orientation.plus(playerPosition));
        aerialOrientationHandler.setRollOrientation(ballFuturePosition.z < hitPositionOnBall.z);
        aerialOrientationHandler.updateOutput(input);

        // jump to the destination if we're on the ground
        if (jumpHandler.isJumpFinished()) {
            if(input.car.hasWheelContact) {
                jumpHandler.setJumpType(new ShortJump());
            }
            else {
                jumpHandler.setJumpType(new SimpleJump());
            }
        }
        jumpHandler.updateJumpState(
                input,
                output,
                CarDestination.getLocal(
                        orientation.minus(playerPosition),
                        input
                ),
                new Vector3()
        );
        output.jump(jumpHandler.getJumpState());
    }

    @Override
    public void updatePidValuesAndArbitraries() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {

        renderer.drawLine3d(Color.green, input.car.position, orientation.plus(input.car.position));
        /* draw cool 3D X "hit" thingy */ {
            renderer.drawLine3d(Color.red, hitPositionOnBall.plus(new Vector3(20, 20, 20)), hitPositionOnBall.plus(new Vector3(-20, -20, -20)));
            renderer.drawLine3d(Color.red, hitPositionOnBall.plus(new Vector3(-20, 20, 20)), hitPositionOnBall.plus(new Vector3(20, -20, -20)));
            renderer.drawLine3d(Color.red, hitPositionOnBall.plus(new Vector3(20, -20, 20)), hitPositionOnBall.plus(new Vector3(-20, 20, -20)));
            renderer.drawLine3d(Color.red, hitPositionOnBall.plus(new Vector3(20, 20, -20)), hitPositionOnBall.plus(new Vector3(-20, -20, 20)));
        }
        renderer.drawLine3d(Color.CYAN, ballFuturePosition, hitPositionOnBall);
    }
}
