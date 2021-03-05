package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.directionnal_hit.parabola;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.controllers.BoostController;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class AerialDirectionalHit6 extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController5 aerialOrientationHandler;
    private JumpController jumpController;
    private BoostController boostController;

    private Vector3 ballDestination;
    private Vector3 ballOffset;
    private Vector3 directionOfTopOfHitableHalfSphereSurfaceOnBall;

    private Trajectory3D naiveBallTrajectory;
    private Trajectory3D ballTrajectoryWithCarHitBox;
    private Trajectory3D carPredictedTrajectory;
    private AerialAccelerationFinder aerialAccelerationFinder;
    private AerialTrajectoryInfo aerialInfo;

    private Vector3 futureBallPosition;
    private Vector3 futureCarPosition;
    private HitBox futureHitBox;

    public AerialDirectionalHit6(BotBehaviour bot) {
        this.bot = bot;
        this.aerialOrientationHandler = new AerialOrientationController5(bot);
        this.jumpController = new JumpController(bot);
        this.boostController = new BoostController();

        this.ballDestination = new Vector3();
        this.ballOffset = new Vector3();
        this.directionOfTopOfHitableHalfSphereSurfaceOnBall = new Vector3();

        this.naiveBallTrajectory = null;
        this.ballTrajectoryWithCarHitBox = null;
        this.carPredictedTrajectory = new Parabola3D(new Vector3(), new Vector3(), new Vector3(), 0);
        this.aerialAccelerationFinder = null;
        this.aerialInfo = new AerialTrajectoryInfo();

        this.futureBallPosition = new Vector3();
        this.futureCarPosition = new Vector3();
        this.futureHitBox = null;
    }

    public void setBallDestination(Vector3 destination) {
        this.ballDestination = destination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();

        aerialInfo = findAerialTrajectoryInfo(input);
        Vector3 orientation = aerialInfo.acceleration;

        // orientation handling
        aerialOrientationHandler.setNoseOrientation(orientation);
        //aerialOrientationHandler.setRollOrientation(ballTrajectoryWithCarHitBox.compute(aerialInfo.timeOfFlight));
        aerialOrientationHandler.setRollOrientation(new Vector3(0, 0, 10000));
        aerialOrientationHandler.updateOutput(input);

        // boost
        output.boost(input.car.orientation.noseVector.dotProduct(orientation.normalized()) > 0.7
        && boostController.process(aerialInfo.acceleration.magnitude()));

        // jump
        this.jumpController.setFirstJumpType(new SimpleJump(), input);
        if(input.ball.position.z > 300) {
            this.jumpController.setSecondJumpType(new ShortJump(), input);
        }
        else {
            this.jumpController.setSecondJumpType(new Wait(), input);
        }
        this.jumpController.updateOutput(input);
    }

    private void updateNaiveBallTrajectory(DataPacket input) {
        naiveBallTrajectory = time -> {
            Vector3 futureBallPosition = input.statePrediction.ballAtTime(time).position;
            return futureBallPosition.plus(futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS));
            //return new Vector3(0, 0, 1000);
        };
    }

    private void updateBallTrajectoryWithCarHitBox(DataPacket input) {
        directionOfTopOfHitableHalfSphereSurfaceOnBall = input.statePrediction.ballAtTime(aerialInfo.timeOfFlight).velocity
                .minus(carPredictedTrajectory.derivative(aerialInfo.timeOfFlight)).normalized();

        futureHitBox = input.car.hitBox.generateHypotheticalHitBox(carPredictedTrajectory.apply(aerialInfo.timeOfFlight));
        Vector3 hitPointOnCar = futureHitBox.closestPointOnSurface(input.statePrediction.ballAtTime(aerialInfo.timeOfFlight).position);
        Vector3 carOffset = hitPointOnCar.minus(carPredictedTrajectory.apply(aerialInfo.timeOfFlight));

        ballTrajectoryWithCarHitBox = time -> {
            Vector3 futureBallPosition = RawBallTrajectory.ballAtTime(time).position;
            //Vector3 futureBallPosition = new Vector3(0, 0, 1000);
            ballOffset = futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS);
            if(directionOfTopOfHitableHalfSphereSurfaceOnBall.dotProduct(ballOffset.normalized()) < 0) {
                ballOffset = ballOffset.minus(ballOffset.projectOnto(directionOfTopOfHitableHalfSphereSurfaceOnBall)).scaledToMagnitude(RlConstants.BALL_RADIUS);
            }

            return futureBallPosition.plus(ballOffset).minus(carOffset);
        };
    }

    private AerialTrajectoryInfo findAerialTrajectoryInfo(DataPacket input) {
        updateNaiveBallTrajectory(input);
        aerialAccelerationFinder = new AerialAccelerationFinder(naiveBallTrajectory);
        aerialInfo = aerialAccelerationFinder.findAerialTrajectoryInfo(0, input.car);

        carPredictedTrajectory = new Parabola3D(
                input.car.position,
                input.car.velocity,
                this.aerialInfo.acceleration.minus(new Vector3(0, 0, RlConstants.NORMAL_GRAVITY_STRENGTH)),
                0
        );

        for(int i = 0; i < 1; i++) {
            updateBallTrajectoryWithCarHitBox(input);
            aerialAccelerationFinder = new AerialAccelerationFinder(ballTrajectoryWithCarHitBox);
            aerialInfo = aerialAccelerationFinder.findAerialTrajectoryInfo(0, input.car);

            carPredictedTrajectory = new Parabola3D(
                    input.car.position,
                    input.car.velocity,
                    this.aerialInfo.acceleration.minus(new Vector3(0, 0, RlConstants.NORMAL_GRAVITY_STRENGTH)),
                    0
            );

            futureHitBox = input.car.hitBox.generateHypotheticalHitBox(carPredictedTrajectory.apply(aerialInfo.timeOfFlight));
        }

        return aerialInfo;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position.toFlatVector(), input.car.position.plus(aerialInfo.acceleration.scaledToMagnitude(300)).toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(ballDestination, Color.MAGENTA);
        shapeRenderer.renderCross(futureCarPosition, Color.red);

        shapeRenderer.renderCross(new Vector3(0, 0, 1000), Color.red);



        shapeRenderer.renderTrajectory(carPredictedTrajectory, -0.5, 0, Color.pink);
        shapeRenderer.renderTrajectory(carPredictedTrajectory, 0, 4, Color.CYAN);
        shapeRenderer.renderHitBox(futureHitBox, Color.YELLOW);

        renderer.drawLine3d(Color.CYAN, directionOfTopOfHitableHalfSphereSurfaceOnBall.scaledToMagnitude(300).plus(input.ball.position).toFlatVector(), input.ball.position.toFlatVector());
        renderer.drawLine3d(Color.green, ballOffset.scaledToMagnitude(300).plus(input.ball.position).toFlatVector(), input.ball.position.toFlatVector());
    }
}
