package rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DriveToPredictedBallBounceController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.controllers.PidController;
import util.controllers.ThrottleController;
import util.vector.Vector2;
import util.vector.Vector3;

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

        steerPid = new PidController(2, 0, 20);

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
        botBehaviour.output().boost(rawThrottlingAmount > 100);
    }

    private Vector3 findOptimalBallPositionOnPlayerCar(DataPacket input) {
        neutralBallDestination = input.car.position.plus(new Vector3(0, 0, 50));
        Vector3 optimalLeftAndRightOffset = findLeftAndRightBallOffsetFromCar(input);
        Vector3 optimalFrontAndBackOffset = findFrontAndBackBallOffsetFromCar(input);
        return neutralBallDestination.plus(optimalLeftAndRightOffset).plus(optimalFrontAndBackOffset);
    }

    private Vector3 findLeftAndRightBallOffsetFromCar(DataPacket input) {
        // right is positive, and left is negative. Just a convention
        double maximumTuringDistance = 200;
        double optimalLeftAndRightOffsetAmount = -input.car.orientation.noseVector.flatten().correctionAngle(ballDestination.minus(input.car.position).flatten());
        optimalLeftAndRightOffsetAmount /= Math.PI*2;   // normalize
        optimalLeftAndRightOffsetAmount *= 250;         // arbitrary max offset (we need a scaling anyway, this needs tweaking)
        optimalLeftAndRightOffsetAmount = clamp(optimalLeftAndRightOffsetAmount, -maximumTuringDistance, maximumTuringDistance);

        return input.car.orientation.rightVector.scaled(optimalLeftAndRightOffsetAmount);
    }

    private Vector3 findFrontAndBackBallOffsetFromCar(DataPacket input) {
        // front is positive, back negative
        double sensitivityOfSpeedDifference = 200;      // this value is arbitrary, and is tweakable to the desired "aggressivity" of corrections
        double optimalFrontAndBackOffsetAmount = clamp(ballTargetSpeed - input.ball.velocity.magnitude(), -sensitivityOfSpeedDifference, sensitivityOfSpeedDifference);
        optimalFrontAndBackOffsetAmount /= sensitivityOfSpeedDifference;    // normalize
        optimalFrontAndBackOffsetAmount *= 20;                              // arbitrary offset, needs tweaking

        return input.car.orientation.noseVector.scaled(optimalFrontAndBackOffsetAmount);
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

        renderer.drawLine3d(Color.CYAN, ballDestination, input.ball.position);

        //renderer.drawLine3d(Color.red, steeringDestination, input.car.position);
    }
}
