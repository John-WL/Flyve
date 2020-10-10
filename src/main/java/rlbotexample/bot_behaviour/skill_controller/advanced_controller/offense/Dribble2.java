package rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.controllers.PidController;
import util.math.vector.Vector3;

import java.awt.*;

public class Dribble2 extends SkillController {

    private BotBehaviour botBehaviour;
    private Vector3 ballDestination;
    private double ballTargetSpeed;
    private Vector3 neutralBallDestination;
    private Vector3 desiredBallPositionOnPlayerCar;

    PidController steerPid;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    public Dribble2(BotBehaviour bot) {
        botBehaviour = bot;
        ballDestination = new Vector3();
        ballTargetSpeed = 1200;
        neutralBallDestination = new Vector3();
        desiredBallPositionOnPlayerCar = new Vector3();

        steerPid = new PidController(1.7, 0, 17);

        drivingSpeedController = new DrivingSpeedController(bot);
        groundOrientationController = new GroundOrientationController(bot);
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    public void setBallSpeed(double ballTargetSpeed) {
        this.ballTargetSpeed = ballTargetSpeed;
    }

    @Override
    public void updateOutput(DataPacket input) {
        desiredBallPositionOnPlayerCar = findOptimalBallPositionOnPlayerCar(input);
        final Vector3 playerDeltaDestination = input.ball.position.minus(desiredBallPositionOnPlayerCar);

        final double rawSteeringAmount = playerDeltaDestination.plus(input.car.orientation.noseVector.scaled(200)).flatten().correctionAngle(input.car.orientation.noseVector.flatten());
        /*steeringDestination =
        groundOrientationController.setDestination(steeringDestination);
        groundOrientationController.updateOutput(input);*/
        double steerAmount = steerPid.process(rawSteeringAmount, 0);
        botBehaviour.output().steer(steerAmount);
        //botBehaviour.output().drift(steerAmount > 2.5);


        double rawThrottlingAmount = playerDeltaDestination.dotProduct(input.car.orientation.noseVector)*10;
        if(rawThrottlingAmount < 0) {
            //rawThrottlingAmount = Math.min(-rawThrottlingAmount, 100);
        }
        drivingSpeedController.setSpeed(input.ball.velocity.flatten().magnitude() + rawThrottlingAmount);
        drivingSpeedController.updateOutput(input);
        botBehaviour.output().boost(rawThrottlingAmount > 200);
    }

    private Vector3 findOptimalBallPositionOnPlayerCar(DataPacket input) {
        neutralBallDestination = input.car.position.plus(new Vector3(0, 0, 50));
        Vector3 optimalLeftAndRightOffset = findLeftAndRightBallOffsetFromCar(input);
        Vector3 optimalFrontAndBackOffset = findFrontAndBackBallOffsetFromCar(input);
        return neutralBallDestination.plus(optimalLeftAndRightOffset).plus(optimalFrontAndBackOffset);
    }

    private Vector3 findFrontAndBackBallOffsetFromCar(DataPacket input) {
        // front is positive, back negative
        double sensitivityOfSpeedError = 600;      // this value is arbitrary, and is tweakable to the desired "aggressivity" of corrections
        double allowedRangeOfBallPositionOnCar = 80;                       // arbitrary range, needs tweaking
        double optimalFrontAndBackOffsetAmount = clamp((ballTargetSpeed - input.ball.velocity.magnitude())*speedFactorToConvergeToDestination(input), -sensitivityOfSpeedError, sensitivityOfSpeedError);
        optimalFrontAndBackOffsetAmount /= sensitivityOfSpeedError;    // normalize
        optimalFrontAndBackOffsetAmount *= allowedRangeOfBallPositionOnCar;

        return input.car.orientation.noseVector.scaled(optimalFrontAndBackOffsetAmount);
    }

    private Vector3 findLeftAndRightBallOffsetFromCar(DataPacket input) {
        // right is positive, and left is negative. Just a convention
        double sensitivityOfDirectionError = 5;
        double allowedRangeOfBallPositionOnCar = 110;
        double optimalLeftAndRightOffsetAmount = clamp(-input.car.orientation.noseVector.flatten().correctionAngle((ballDestination.plus(deltaVectorToConvergeToDestination(input))).minus(input.car.position).flatten()),
                -sensitivityOfDirectionError, sensitivityOfDirectionError);
        optimalLeftAndRightOffsetAmount /= sensitivityOfDirectionError;   // normalize
        optimalLeftAndRightOffsetAmount *= allowedRangeOfBallPositionOnCar;         // arbitrary max offset (we need a scaling anyway, this needs tweaking)

        return input.car.orientation.rightVector.scaled(optimalLeftAndRightOffsetAmount);
    }

    private double speedFactorToConvergeToDestination(DataPacket input) {
        return 1;
    }

    private Vector3 deltaVectorToConvergeToDestination(DataPacket input) {
        Vector3 deltaDestination = input.ball.velocity.scaled(-3)
                .projectOnto(input.ball.position.minus(ballDestination).minusAngle(new Vector3(0, 1, 0)));
        double maxDeltaFactor = 0.8;
        if(deltaDestination.magnitude() > input.ball.position.minus(ballDestination).magnitude()*maxDeltaFactor) {
            deltaDestination = deltaDestination.scaledToMagnitude(input.ball.position.minus(ballDestination).magnitude()*maxDeltaFactor);
        }

        return deltaDestination;
    }


    private double clamp(double x, double min, double max) {
        return Math.max(Math.min(x, max), min);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.red, neutralBallDestination, desiredBallPositionOnPlayerCar);
        renderer.drawRectangle3d(Color.red, desiredBallPositionOnPlayerCar, 10, 10, true);

        renderer.drawLine3d(Color.yellow, neutralBallDestination, input.car.position);
        renderer.drawRectangle3d(Color.yellow, neutralBallDestination, 10, 10, true);

        renderer.drawLine3d(Color.CYAN, ballDestination, input.ball.position);

        renderer.drawLine3d(Color.magenta, ballDestination.plus(deltaVectorToConvergeToDestination(input)), input.ball.position);

        //renderer.drawLine3d(Color.red, steeringDestination, input.car.position);
    }
}
