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
import util.parameter_configuration.ArbitraryValueSerializer;
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

    private double distanceFrontBackCoef = -0.35;
    private double velocityFrontBackCoef = -0.11;
    private double distanceLeftRightCoef = -0.86;
    private double velocityLeftRightCoef = -0.28;

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
        boolean isBoosting = boostController.process(
                130
                + ballDestination.minus(input.ball.position).z
                + input.ball.position.minus(input.car.position).z
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

    private Vector3 findOptimalNoseOrientationDestination(DataPacket input) {
        return findNeutralNoseDestination(input);
                //.plus(findOptimalDeltaPositionOnCar(input).scaled(1, 1, 0));
    }

    private Vector3 findNeutralNoseDestination(DataPacket input) {
        Vector3 frontDribbleDirection = Vector3.UP_VECTOR.crossProduct(input.car.orientation.rightVector);
        Vector3 sideDribbleDirection = Vector3.UP_VECTOR.crossProduct(input.car.orientation.roofVector);

        Vector3 frontDribbleNoseDestination = getPositionFromBall(input)
                .scaled(distanceFrontBackCoef).scaled(1, 1, 0)
            .plus(getVelocityFromBall(input)
                .scaled(velocityFrontBackCoef).scaled(1, 1, 0))
            .projectOnto(frontDribbleDirection);

        Vector3 sideDribbleNoseDestination = getPositionFromBall(input)
                .scaled(distanceLeftRightCoef).scaled(1, 1, 0)
            .plus(getVelocityFromBall(input)
                .scaled(velocityLeftRightCoef).scaled(1, 1, 0))
            .projectOnto(sideDribbleDirection);

        Vector3 noseOrientation = input.ball.position
            .minus(new Vector3(0, 0, RlConstants.BALL_RADIUS))
            .plus(input.car.orientation.roofVector.scaled(-1).scaled(4))
            .plus(frontDribbleNoseDestination)
            .plus(sideDribbleNoseDestination);

        if(noseOrientation.projectOnto(frontDribbleDirection).dotProduct(frontDribbleDirection) > 0) {

        }

        return noseOrientation;
    }

    private Vector3 findOptimalRollOrientationDestination(DataPacket input) {
        return new Vector3(0, 0, 100);
        //return input.ball.position;
    }

    private Vector3 getPositionFromBall(DataPacket input) {
        return input.car.position.minus(input.ball.position);
    }

    private Vector3 getVelocityFromBall(DataPacket input) {
        return input.car.velocity.minus(input.ball.velocity);
    }

    @Override
    public void setupController() {
        distanceFrontBackCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_DISTANCE_FRONT_BACK_COEF);
        velocityFrontBackCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_VELOCITY_FRONT_BACK_COEF);
        distanceLeftRightCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_DISTANCE_LEFT_RIGHT_COEF);
        velocityLeftRightCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_VELOCITY_LEFT_RIGHT_COEF);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(ballDestination, Color.red);

        aerialOrientationHandler.debug(renderer, input);
        shapeRenderer.renderHitBox(input.car.hitBox, Color.YELLOW);
    }
}
