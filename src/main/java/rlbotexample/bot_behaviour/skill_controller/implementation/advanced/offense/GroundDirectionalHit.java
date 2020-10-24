package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.HitBox;
import util.controllers.PidController;
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

    public GroundDirectionalHit(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();
        this.carDestination = new Vector3();
        this.playerDestinationOnBall = new Vector3();
        this.playerTimeBeforeHittingTheBall = 0;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        playerDestinationOnBall = findOptimalPlayerDestinationOnBall(input);
        playerTimeBeforeHittingTheBall = findOptimalTimeBeforeHittingTheBall(input);
        playerDestinationOnBall = findOptimalPlayerDestinationOnBall(input);

        double driveSpeed = input.ball.velocity.magnitude() + input.car.position.minus(playerDestinationOnBall).dotProduct(input.car.position.minus(input.ball.position).normalized())*1.5;
        drivingSpeedController.setSpeed(driveSpeed);
        drivingSpeedController.updateOutput(input);
        botBehaviour.output().boost(driveSpeed > 1400);

        groundOrientationController.setDestination(playerDestinationOnBall);
        groundOrientationController.updateOutput(input);
    }

    private double findOptimalTimeBeforeHittingTheBall(DataPacket input) {
        double speed = -input.car.velocity.minus(input.ball.velocity).dotProduct(input.car.position.minus(input.ball.position).normalized());
        double distance = input.car.hitBox.projectPointOnSurface(input.ball.position).minus(input.ball.position).magnitude();
        double timeBeforeReachingBall = distance/speed;

        return 0;
    }

    private Vector3 findOptimalPlayerDestinationOnBall(DataPacket input) {
        Vector3 futureBallPosition = input.ballPrediction.ballAtTime(playerTimeBeforeHittingTheBall).position;
        Vector3 desiredHitPositionOnBall = futureBallPosition.plus(futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS-4));
        Vector3 alignedHitBoxCenterPosition = futureBallPosition.minus(ballDestination).scaledToMagnitude(2*RlConstants.BALL_RADIUS);
        Vector3 hitBoxClosestRadiusTowardsBall = input.car.hitBox
                .generateHypotheticalHitBox(alignedHitBoxCenterPosition)
                .projectPointOnSurface(futureBallPosition)
                .minus(alignedHitBoxCenterPosition);
        HitBox bestHitBoxGuess = input.car.hitBox.generateHypotheticalHitBox(desiredHitPositionOnBall.minus(hitBoxClosestRadiusTowardsBall));


        int resolution = 20;
        for(int i = 0; i < resolution; i++) {
            hitBoxClosestRadiusTowardsBall = bestHitBoxGuess
                    .projectPointOnSurface(futureBallPosition)
                    .minus(bestHitBoxGuess.centerPosition);
            bestHitBoxGuess = input.car.hitBox.generateHypotheticalHitBox(desiredHitPositionOnBall.minus(hitBoxClosestRadiusTowardsBall));
        }

        futureHitBox = bestHitBoxGuess;

        return bestHitBoxGuess.centerPosition;
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
