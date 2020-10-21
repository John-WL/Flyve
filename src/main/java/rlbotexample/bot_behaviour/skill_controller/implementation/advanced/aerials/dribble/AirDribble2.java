package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class AirDribble2 extends SkillController {

    private BotBehaviour botBehaviour;
    private Vector3 ballDestination;
    private double ballTargetSpeed;
    private Vector3 ballDestinationOnCar;
    private Vector3 neutralBallPositionOnCar;
    private Vector3 previousCarVelocity;
    private Vector3 previousBallVelocity;

    private AerialOrientationController2 aerialOrientationHandler;

    public AirDribble2(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();
        this.ballTargetSpeed = 500;
        this.ballDestinationOnCar = new Vector3();
        this.neutralBallPositionOnCar = new Vector3();
        this.previousCarVelocity = new Vector3();
        this.previousBallVelocity = new Vector3();

        this.aerialOrientationHandler = new AerialOrientationController2(bot);
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        ballDestinationOnCar = findOptimalBallPositionOnCar(input);

        Vector3 noseDestination = findOptimalNoseOrientationDestination(input);
        Vector3 rollDestination = findOptimalRollOrientationDestination(input);

        aerialOrientationHandler.setOrientationDestination(noseDestination);
        aerialOrientationHandler.setRollOrientation(rollDestination);
        aerialOrientationHandler.updateOutput(input);

        boolean isBoosting = findOptimalBoostValue(input);
        botBehaviour.output().boost(isBoosting);
    }

    private Vector3 findOptimalBallPositionOnCar(DataPacket input) {
        neutralBallPositionOnCar = findNeutralBallPosition(input);
        Vector3 deltaPositionOnCar = findOptimalDeltaPositionOnCar(input);
        return neutralBallPositionOnCar.plus(deltaPositionOnCar);
    }

    private Vector3 findNeutralBallPosition(DataPacket input) {
        return input.car.position
                .plus(new Vector3(0, 0, findCarHitBoxNoseLength(input) + RlConstants.BALL_RADIUS));
    }

    private double findCarHitBoxNoseLength(DataPacket input) {
        return input.car.position.minus(
                input.car.hitBox.projectPointOnSurface(
                        input.car.position.plus(input.car.orientation.noseVector.scaled(200))))
                .magnitude();
    }

    private Vector3 findOptimalDeltaPositionOnCar(DataPacket input) {
        Vector3 desiredBallVelocityTowardsDestination = ballDestination.minus(input.ball.position).scaledToMagnitude(ballTargetSpeed);
        Vector3 extraBallVelocity = input.ball.velocity.minus(desiredBallVelocityTowardsDestination);

        double sensitivity = 0.05;
        double maxOffset = 1;

        Vector3 deltaPositionOnCar = extraBallVelocity.scaled(-sensitivity);
        if(deltaPositionOnCar.magnitude() > maxOffset) {
            deltaPositionOnCar = deltaPositionOnCar.scaledToMagnitude(maxOffset);
        }

        return deltaPositionOnCar;
    }

    /*private Vector3 findOptimalNoseOrientationDestination(DataPacket input) {
        Vector3 desiredVelocityFromBall = ballDestinationOnCar.minus(input.ball.position).scaled(0.02);
        Vector3 desiredAcceleration = desiredVelocityFromBall.minus(getVelocityFromBall(input)).scaled(0.3);
        return findNeutralNoseDestination(input)
                .plus(desiredAcceleration.scaled(1, 1, 0));
    }*/
    private Vector3 findOptimalNoseOrientationDestination(DataPacket input) {
        return findNeutralNoseDestination(input);
                //.plus(input.ball.position.minus(ballDestinationOnCar).scaled(1, 1, 0));
    }

    private Vector3 findNeutralNoseDestination(DataPacket input) {
        return input.ball.position
            .minus(new Vector3(0, 0, RlConstants.BALL_RADIUS))
                .plus(getPositionFromBall(input).scaled(-0.22, -0.22, 0.02))
                .plus(getVelocityFromBall(input).scaled(-0.35, -0.35, 0.05));
                //.plus(getAccelerationFromBall(input).scaled(-0.008, -0.008, 0));
    }

    private Vector3 findOptimalRollOrientationDestination(DataPacket input) {
        return input.car.position.plus(new Vector3(0, 0, 100));
        //return input.ball.position;
    }

    private boolean findOptimalBoostValue(DataPacket input) {
        return getVelocityFromBall(input)
                .dotProduct(getPositionFromBall(input).normalized())
                > -(70 + ballDestinationOnCar.minus(neutralBallPositionOnCar).z*300);
        /*return getVelocityFromBall(input)
                .dotProduct(getPositionFromBall(input).normalized()) > -50;*/
    }

    private Vector3 getPositionFromBall(DataPacket input) {
        return input.car.position.minus(input.ball.position);
    }

    private Vector3 getVelocityFromBall(DataPacket input) {
        return input.car.velocity.minus(input.ball.velocity);
    }

    // oof
    private Vector3 getAccelerationFromBall(DataPacket input) {
        Vector3 accelerationFromBall = input.car.velocity.minus(previousCarVelocity)
                .minus(input.ball.velocity.minus(previousBallVelocity))
                .scaled(RlConstants.BOT_REFRESH_RATE);
        previousCarVelocity = input.car.velocity;
        previousBallVelocity = input.ball.velocity;
        
        return accelerationFromBall;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(ballDestination, Color.red);

        renderer.drawLine3d(Color.blue, input.car.position, neutralBallPositionOnCar);
        shapeRenderer.renderCross(neutralBallPositionOnCar, Color.blue);

        renderer.drawLine3d(Color.green, input.car.position, ballDestinationOnCar);
        shapeRenderer.renderCross(ballDestinationOnCar, Color.green);

        //Vector3 accelerationIncentive = input.ball.position.plus(new Vector3(0, 0, -RlConstants.BALL_RADIUS)).minus(input.car.hitBox.projectPointOnSurface(input.car.position.plus(input.car.orientation.noseVector.scaled(200))));
        //renderer.drawLine3d(Color.BLUE, input.car.position, input.car.position.plus(accelerationIncentive.scaled(100)));


        Vector3 desiredVelocityFromBall = ballDestinationOnCar.minus(input.ball.position);
        Vector3 desiredAcceleration = getVelocityFromBall(input).minus(desiredVelocityFromBall).scaled(1, 1, 0);
        renderer.drawLine3d(Color.YELLOW, input.car.position.plus(new Vector3(0, 0, -100)), input.car.position.plus(new Vector3(0, 0, -100)).plus(desiredAcceleration));
        renderer.drawRectangle3d(Color.YELLOW, input.car.position.plus(new Vector3(0, 0, -100)).plus(desiredAcceleration), 10, 10, true);
    }
}
