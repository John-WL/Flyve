package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController5;
import rlbotexample.input.dynamic_data.DataPacket;
import util.controllers.BoostController;
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

    private AerialOrientationController5 aerialOrientationHandler;
    private BoostController boostController;

    public AirDribble2(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();
        this.ballTargetSpeed = 300;
        this.ballDestinationOnCar = new Vector3();
        this.neutralBallPositionOnCar = new Vector3();
        this.previousCarVelocity = new Vector3();
        this.previousBallVelocity = new Vector3();

        this.aerialOrientationHandler = new AerialOrientationController5(bot);

        this.boostController = new BoostController();
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        ballDestinationOnCar = findOptimalBallPositionOnCar(input);

        Vector3 noseDestination = findOptimalNoseOrientationDestination(input);
        //Vector3 rollDestination = findOptimalRollOrientationDestination(input);
        //Vector3 rollDestination = input.car.position.plus(input.car.orientation.roofVector);

        aerialOrientationHandler.setNoseOrientation(noseDestination.minus(input.car.position));
        aerialOrientationHandler.setRollOrientation(new Vector3(0, 1, 0));
        aerialOrientationHandler.updateOutput(input);

        //boolean isBoosting = findOptimalBoostValue(input);
        boolean isBoosting = boostController.process(130 +
                ballDestination.minus(input.ball.position).z
                - input.ball.velocity.z/1.5);
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
        double sensitivity = 0.001;
        double maxOffset = 2;

        Vector3 deltaPositionOnCar = ballDestination.minus(input.ball.position).scaled(sensitivity);
        if(deltaPositionOnCar.magnitude() > maxOffset) {
            deltaPositionOnCar = deltaPositionOnCar.scaledToMagnitude(maxOffset);
        }

        return deltaPositionOnCar.scaled(-1);
    }

    /*private Vector3 findOptimalNoseOrientationDestination(DataPacket input) {
        Vector3 desiredVelocityFromBall = ballDestinationOnCar.minus(input.ball.position).scaled(0.02);
        Vector3 desiredAcceleration = desiredVelocityFromBall.minus(getVelocityFromBall(input)).scaled(0.3);
        return findNeutralNoseDestination(input)
                .plus(desiredAcceleration.scaled(1, 1, 0));
    }*/
    private Vector3 findOptimalNoseOrientationDestination(DataPacket input) {
        return findNeutralNoseDestination(input);
                //.plus(findOptimalDeltaPositionOnCar(input).scaled(1, 1, 0));
    }

    private Vector3 findNeutralNoseDestination(DataPacket input) {
        Vector3 closestPointOnCarHitBox = input.car.hitBox.projectPointOnSurface(input.ball.position);
        Vector3 rotator = closestPointOnCarHitBox.crossProduct(input.car.orientation.noseVector).scaledToMagnitude(closestPointOnCarHitBox.angle(input.car.orientation.noseVector));
        Vector3 delta = input.car.orientation.noseVector.rotate(rotator).minus(input.car.orientation.noseVector).scaled(20);

        Vector3 noseDestination = input.ball.position
            .minus(new Vector3(0, 0, RlConstants.BALL_RADIUS))
            .plus(input.car.orientation.roofVector.scaled(-1).scaled(3))
            //.plus(delta)
            .plus(getPositionFromBall(input).scaled(-0.42).scaled(1, 1, 0)
            .plus(getVelocityFromBall(input).scaled(-0.11).scaled(1, 1, 0))
                    .projectOnto(Vector3.UP_VECTOR.crossProduct(input.car.orientation.rightVector))
            .plus(getPositionFromBall(input).scaled(-0.7).scaled(1, 1, 0)
            .plus(getVelocityFromBall(input).scaled(-0.28).scaled(1, 1, 0))
                  .projectOnto(Vector3.UP_VECTOR.crossProduct(input.car.orientation.roofVector)))
            );

        if(input.car.orientation.roofVector.dotProduct(noseDestination.minus(input.car.position)) > 0) {
            noseDestination = noseDestination.minus(input.ball.position).scaled(0.75).plus(input.ball.position);
        }

        return noseDestination;
    }

    private Vector3 findOptimalRollOrientationDestination(DataPacket input) {
        return new Vector3(0, 0, 100);
        //return input.ball.position;
    }

    private boolean findOptimalBoostValue(DataPacket input) {
        return getVelocityFromBall(input)
                .dotProduct(getPositionFromBall(input).normalized())
                + getPositionFromBall(input).magnitude()
                > -(50 + ballDestinationOnCar.minus(neutralBallPositionOnCar).z*200);
        /*return getVelocityFromBall(input)
                .dotProduct(getPositionFromBall(input).normalized()) > -50;*/
    }

    private Vector3 getPositionFromBall(DataPacket input) {
        return input.car.position.minus(input.ball.position);
    }

    private Vector3 getVelocityFromBall(DataPacket input) {
        return input.car.velocity.minus(input.ball.velocity);
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

        aerialOrientationHandler.debug(renderer, input);
        shapeRenderer.renderHitBox(input.car.hitBox, Color.YELLOW);
    }
}
