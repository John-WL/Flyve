package rlbotexample.bot_behaviour.skill_controller.advanced_controller;

import rlbot.cppinterop.RLBotDll;
import rlbot.flat.BallPrediction;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.AerialOrientationHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.JumpHandler;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.jump.implementations.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.BallPredictionHelper;
import rlbotexample.input.prediction.Predictions;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
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

        // try to predict the point in time with which we should try to hit the ball
        /*
        // first try
        double timeBeforeReachingBall = input.ball.position.minus(playerPosition).magnitude()/(input.ball.velocity.minus(playerSpeed).magnitude());
        if(timeBeforeReachingBall > 6) {
            timeBeforeReachingBall = 6;
        }*/

        // second try
        // this is the player speed SIGNED (it's the player speed, but it's negative if it's going away from the ball...)
        double signedPlayerSpeedFromBall = playerSpeedFromBall.magnitude() *(
                playerSpeedFromBall.dotProduct(playerDistanceFromBall)/(playerDistanceFromBall.magnitude()*playerSpeedFromBall.magnitude())
        );
        double a = -RlConstants.ACCELERATION_DUE_TO_BOOST/2;
        double b = signedPlayerSpeedFromBall;
        double c = playerDistanceFromBall.magnitude();
        double timeBeforeReachingBall = -b - Math.sqrt(b*b - 4*a*c);
        timeBeforeReachingBall /= 2*a;

        if(timeBeforeReachingBall > 3) {
            timeBeforeReachingBall = 3;
        }
        // get the future player and ball positions
        Vector3 playerFuturePosition = input.ball.position;
        if(input.ball.velocity.magnitude() > 0.1) {
            playerFuturePosition = Predictions.aerialPlayerPosition(playerPosition, playerSpeed, timeBeforeReachingBall);
        }
        Vector3 ballFuturePosition = Predictions.ballPositon(input.ball.position, timeBeforeReachingBall);

        // get the ball offset so we actually hit the ball to make it go in the desired direction
        Vector3 ballOffset = ballFuturePosition.minus(ballDestination.plus(new Vector3(0, 0, 1000))).scaledToMagnitude(RlConstants.BALL_RADIUS);

        // get the orientation we should have to hit the ball
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


        // if we're hitting the ball in the future, face the ball to hit it properly
        if(hitPositionOnBall.minus(playerFuturePosition).magnitude() < 50) {
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
        try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            BallPredictionHelper.drawTillMoment(ballPrediction, ballPrediction.slices(0).gameSeconds() + 6, Color.LIGHT_GRAY, renderer);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
