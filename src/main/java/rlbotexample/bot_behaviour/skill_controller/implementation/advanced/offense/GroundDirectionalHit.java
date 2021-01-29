package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Flip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.MiddleJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.HitBox;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GroundDirectionalHit extends SkillController {

    private BotBehaviour botBehaviour;
    private Vector3 ballDestination;
    private Vector3 carDestination;
    private Vector3 playerDestinationOnBall;
    private HitBox futureHitBox = null;
    private double playerTimeBeforeHittingTheBall;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;
    private JumpController jumpController;

    public GroundDirectionalHit(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();
        this.carDestination = new Vector3();
        this.playerDestinationOnBall = new Vector3();
        this.playerTimeBeforeHittingTheBall = 0;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.jumpController = new JumpController(bot);

    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        int resolution = 50;
        for(int i = 0; i < resolution; i++) {
            playerTimeBeforeHittingTheBall = findOptimalTimeBeforeHittingTheBall(input);
            playerDestinationOnBall = findOptimalPlayerDestinationOnBall(input);
        }

        double driveSpeed = 2300;
        drivingSpeedController.setSpeed(driveSpeed);
        drivingSpeedController.updateOutput(input);
        botBehaviour.output().boost(driveSpeed > 1400
                && driveSpeed-50 - input.car.velocity.magnitude() > 0);

        groundOrientationController.setDestination(playerDestinationOnBall);
        groundOrientationController.updateOutput(input);

        if(playerDestinationOnBall.minus(input.car.position).magnitude() > 2000) {
            botBehaviour.output().drift(false);
        }

        if(input.ball.position.minus(input.car.position).magnitude() < 700
        && input.car.velocity.normalized().dotProduct(playerDestinationOnBall.minus(input.car.position).normalized()) > 0.9) {
            jumpController.setFirstJumpType(new MiddleJump(), input);
            jumpController.setSecondJumpType(new Flip(), input);
        }
        else {
            jumpController.setFirstJumpType(new Wait(), input);
            jumpController.setSecondJumpType(new Wait(), input);
        }
        jumpController.setJumpDestination(input.ball.position);
        jumpController.updateOutput(input);
    }

    private double findOptimalTimeBeforeHittingTheBall(DataPacket input) {
        Vector3 offsetCarPosition = input.car.hitBox.closestPointOnSurface(input.ball.position);
        Vector3 offsetBallPosition = input.ball.position.plus(offsetCarPosition.minus(input.ball.position).scaledToMagnitude(RlConstants.BALL_RADIUS));
        double distance = offsetBallPosition.minus(offsetCarPosition).magnitude();
        //double speed = input.ball.velocity.scaled(1, 1, 0).minus(input.car.velocity).magnitude();//.dotProduct(offsetBallPosition.minus(offsetCarPosition).normalized());
        double speed = (input.car.velocity.magnitude()*0.4 + 2300*0.6);
        double timeBeforeHit = distance/speed;

        return timeBeforeHit;
    }

    private Vector3 findOptimalPlayerDestinationOnBall(DataPacket input) {
        Vector3 futureBallPosition = input.statePrediction.ballAtTime(playerTimeBeforeHittingTheBall).position;

        Vector3 ballOffsetAwayFromDestination = futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS);
        Vector3 desiredHitPositionOnBall = futureBallPosition.plus(ballOffsetAwayFromDestination);
        /*
        Vector3 ballOffsetTowardsCar = input.car.position.minus(futureBallPosition).scaledToMagnitude(RlConstants.BALL_RADIUS);
        if(desiredHitPositionOnBall.normalized().dotProduct(ballOffsetTowardsCar.normalized()) < 0) {
            if(ballOffsetTowardsCar.minusAngle(ballOffsetAwayFromDestination).y < 0) {
                desiredHitPositionOnBall = futureBallPosition.plus(ballOffsetTowardsCar.plusAngle(new Vector3(0, 1, 0)));
            }
            else {
                desiredHitPositionOnBall = futureBallPosition.plus(ballOffsetTowardsCar.plusAngle(new Vector3(0, -1, 0)));
            }
        }*/

        Vector3 alignedHitBoxCenterPosition = futureBallPosition.minus(ballDestination).scaledToMagnitude(2*RlConstants.BALL_RADIUS);
        Vector3 hitBoxClosestRadiusTowardsBall = input.car.hitBox
                .generateHypotheticalHitBox(alignedHitBoxCenterPosition)
                .closestPointOnSurface(futureBallPosition)
                .minus(alignedHitBoxCenterPosition);
        HitBox bestHitBoxGuess = input.car.hitBox.generateHypotheticalHitBox(desiredHitPositionOnBall.minus(hitBoxClosestRadiusTowardsBall));

        int resolution = 20;
        for(int i = 0; i < resolution; i++) {
            hitBoxClosestRadiusTowardsBall = bestHitBoxGuess
                    .closestPointOnSurface(futureBallPosition)
                    .minus(bestHitBoxGuess.centerPositionOfHitBox);
            bestHitBoxGuess = input.car.hitBox.generateHypotheticalHitBox(desiredHitPositionOnBall.minus(hitBoxClosestRadiusTowardsBall));
        }

        futureHitBox = bestHitBoxGuess;

        return bestHitBoxGuess.centerPositionOfHitBox;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.CYAN, playerDestinationOnBall, input.car.position);
        renderer.drawLine3d(Color.red, input.ball.position, ballDestination);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderHitBox(futureHitBox, Color.YELLOW);
    }
}
