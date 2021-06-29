package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
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
    private Vector3 smoothedOutBallDestination;
    private double ballTargetSpeed;
    private Vector3 ballDestinationOnCar;
    private Vector3 neutralBallPositionOnCar;
    private Vector3 previousCarVelocity;
    private Vector3 previousBallVelocity;

    private Vector3 deltaPositionOnCar;

    // -0.35
    // -0.105
    // -0.86
    // -0.275
    //private double distanceFrontBackCoef = -0.35;
    //private double velocityFrontBackCoef = -0.105;
    //private double distanceLeftRightCoef = -0.88;
    //private double velocityLeftRightCoef = -0.2735;
    //private double distanceFrontBackCoef = -0.32;
    //private double velocityFrontBackCoef = -0.105;
    //private double distanceLeftRightCoef = -0.88;
    //private double velocityLeftRightCoef = -0.2735;
    private double distanceFrontBackCoef = -0.35;
    private double velocityFrontBackCoef = -0.105;
    private double distanceLeftRightCoef = -0.90;
    private double velocityLeftRightCoef = -0.2735;

    private AerialOrientationController5 aerialOrientationHandler;
    private BoostController boostController;

    // you need to make a new object each time you're starting to aerials #2!
    // we're using a convergent destination, so it won't be equal to zero if we're reusing a previously used one!
    public AirDribble2(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();
        this.smoothedOutBallDestination = new Vector3();
        this.ballTargetSpeed = 1000;
        this.ballDestinationOnCar = new Vector3();
        this.neutralBallPositionOnCar = new Vector3();
        this.previousCarVelocity = new Vector3();
        this.previousBallVelocity = new Vector3();

        this.deltaPositionOnCar = new Vector3();

        this.aerialOrientationHandler = new AerialOrientationController5(bot);

        this.boostController = new BoostController();
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        if(smoothedOutBallDestination.isZero()) {
            smoothedOutBallDestination = ballDestination;
        }
        double convergenceRateForBallDestination = 0.03;
        smoothedOutBallDestination = smoothedOutBallDestination.scaled(1-convergenceRateForBallDestination)
                .plus(ballDestination.scaled(convergenceRateForBallDestination));

        Vector3 noseDestination = findOptimalNoseOrientationDestination(input);
        //Vector3 rollDestination = findOptimalRollOrientationDestination(input);
        //Vector3 rollDestination = input.car.position.plus(input.car.orientation.roofVector);

        aerialOrientationHandler.setNoseOrientation(noseDestination.minus(input.car.position));
        aerialOrientationHandler.setRollOrientation(
                smoothedOutBallDestination.minus(input.ball.position).normalized()
                .plus(input.ball.velocity.normalized())
                .plus(input.car.orientation.roofVector.scaled(11)));
        aerialOrientationHandler.updateOutput(input);

        //boolean isBoosting = findOptimalBoostValue(input);
        boolean isBoosting = boostController.process(
                Math.max(
                        smoothedOutBallDestination.minus(input.ball.position).z
                                + input.ball.position.minus(input.car.position).z / 0.75
                                - input.ball.velocity.z/1.5,
                        50));
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
                input.car.hitBox.closestPointOnSurface(
                        input.car.position.plus(input.car.orientation.noseVector.scaled(200))))
                .magnitude();
    }

    private Vector3 findOptimalDeltaPositionOnCar(DataPacket input) {
        Vector3 desiredBallVelocityTowardsDestination = ballDestination.minus(input.ball.position).scaledToMagnitude(ballTargetSpeed);
        Vector3 extraBallVelocity = input.ball.velocity.minus(desiredBallVelocityTowardsDestination);

        double sensitivity = 0.008;
        double maxOffset = 2;
        double convergenceRate = 0.01;

        deltaPositionOnCar = deltaPositionOnCar.scaled(1-convergenceRate)
            .plus(extraBallVelocity.scaled(-sensitivity).scaled(convergenceRate));
        if(deltaPositionOnCar.magnitude() > maxOffset) {
            deltaPositionOnCar = deltaPositionOnCar.scaledToMagnitude(maxOffset);
        }

        return deltaPositionOnCar.scaled(-1);
    }

    private Vector3 findOptimalNoseOrientationDestination(DataPacket input) {
        return findNeutralNoseDestination(input)
                .plus(findOptimalDeltaPositionOnCar(input).scaled(1, 1, 0));
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
            .plus(input.car.orientation.roofVector.scaled(-1).scaled(3))
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
        //distanceFrontBackCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_DISTANCE_FRONT_BACK_COEF);
        //velocityFrontBackCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_VELOCITY_FRONT_BACK_COEF);
        //distanceLeftRightCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_DISTANCE_LEFT_RIGHT_COEF);
        //velocityLeftRightCoef = ArbitraryValueSerializer.deserialize(ArbitraryValueSerializer.AIR_DRIBBLE_VELOCITY_LEFT_RIGHT_COEF);
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(ballDestination, Color.red);
        shapeRenderer.renderCross(smoothedOutBallDestination, Color.magenta);

        aerialOrientationHandler.debug(renderer, input);
        shapeRenderer.renderHitBox(input.car.hitBox, Color.YELLOW);
    }
}
