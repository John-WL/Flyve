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
import rlbotexample.input.dynamic_data.RlUtils;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.renderers.ShapeRenderer;
import util.vector.Vector3;

import java.awt.*;

public class AerialDirectionalHit extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationHandler aerialOrientationHandler;
    private JumpHandler jumpHandler;
    private Vector3 ballDestination;

    private Vector3 orientation;
    private Vector3 hitPositionOnBall;
    private Vector3 ballFuturePosition;

    public AerialDirectionalHit(BotBehaviour bot) {
        this.bot = bot;
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

        double timeBeforeReachingBall = RlUtils.timeToReachAerialDestination(playerDistanceFromBall, playerSpeedFromBall);

        // get the future player and getNativeBallPrediction positions
        Vector3 playerFuturePosition = input.ball.position;
        if(input.ball.velocity.magnitude() > 0.1) {
            //playerFuturePosition = predictions.aerialKinematicBody(playerPosition, playerSpeed, timeBeforeReachingBall).getPosition();
            playerFuturePosition = new Parabola3D(playerPosition, playerSpeed, new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH), 0).compute(timeBeforeReachingBall);
        }
        Vector3 ballFuturePosition = input.ballPrediction.ballAtTime(timeBeforeReachingBall).position;

        // get the getNativeBallPrediction offset so we actually hit the getNativeBallPrediction to make it go in the desired direction
        Vector3 ballOffset = ballFuturePosition.minus(ballDestination.plus(new Vector3(0, 0, 1000))).scaledToMagnitude(RlConstants.BALL_RADIUS);

        // get the orientation we should have to hit the ball
        Vector3 orientation = ballFuturePosition.plus(ballOffset).minus(playerFuturePosition);

        // update variables so we can print them later in the debugger
        hitPositionOnBall = ballFuturePosition.plus(ballOffset);
        this.orientation = orientation;
        this.ballFuturePosition = ballFuturePosition;

        // boost to the destination
        if(input.car.orientation.noseVector.dotProduct(orientation)/orientation.magnitude() > 0.7) {
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
        aerialOrientationHandler.setRollOrientation(ballFuturePosition);
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
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position, orientation.plus(input.car.position));
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(hitPositionOnBall, Color.red);
        renderer.drawLine3d(Color.CYAN, ballFuturePosition, hitPositionOnBall);
    }
}
