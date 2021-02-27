package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.RlUtils;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class AerialDirectionalHit4 extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController2 aerialOrientationHandler;
    private JumpController jumpController;

    private Vector3 ballDestination;
    private Vector3 orientation;
    private Parabola3D carPredictedTrajectory;
    private double timeToReachAerial;
    private Vector3 futureBallPosition;
    private Vector3 futureCarPosition;
    private HitBox futureHitBox;

    public AerialDirectionalHit4(BotBehaviour bot) {
        this.bot = bot;
        this.aerialOrientationHandler = new AerialOrientationController2(bot);
        this.jumpController = new JumpController(bot);

        this.orientation = new Vector3();
        this.ballDestination = new Vector3();
        this.carPredictedTrajectory = new Parabola3D(new Vector3(), new Vector3(), new Vector3(), 0);
        this.timeToReachAerial = 0;
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

        orientation = findPlayerOrientation(input);

        // orientation
        aerialOrientationHandler.setOrientationDestination(orientation.scaled(300).plus(input.car.position));
        if(timeToReachAerial < 1.5) {
            aerialOrientationHandler.setRollOrientation(input.statePrediction.ballAtTime(timeToReachAerial).position);
        }
        else {
            aerialOrientationHandler.setRollOrientation(input.car.position.plus(input.car.orientation.roofVector));
        }
        if(input.car.position.z < 100) {
            aerialOrientationHandler.setRollOrientation(new Vector3(0, 0, 10000));
        }
        aerialOrientationHandler.updateOutput(input);

        // boost
        output.boost(input.car.orientation.noseVector.dotProduct(orientation) > 0.7);

        // jump
        this.jumpController.setFirstJumpType(new SimpleJump(), input);
        this.jumpController.setSecondJumpType(new ShortJump(), input);
        this.jumpController.updateOutput(input);
    }

    private Vector3 findPlayerOrientation(DataPacket input) {
        Vector3 naiveOrientationGuess = input.ball.position.minus(input.car.position).normalized();
        updateCarPredictedTrajectory(naiveOrientationGuess, input);
        timeToReachAerial = findTimeOfClosestApproachToBall(input);
        futureBallPosition = computeBallTrajectory(timeToReachAerial, input);
        futureCarPosition = carPredictedTrajectory.compute(timeToReachAerial);
        Vector3 aerialDistanceError = futureBallPosition
                .minus(futureCarPosition);
        Vector3 deltaOrientationToConverge = aerialDistanceError.scaled(1/input.car.position.minus(futureBallPosition).magnitude());
        naiveOrientationGuess = naiveOrientationGuess.plus(deltaOrientationToConverge).normalized();

        int precision = 50;
        for(int i = 0; i < precision; i++) {
            updateCarPredictedTrajectory(naiveOrientationGuess, input);
            timeToReachAerial = findTimeOfClosestApproachToBall(input);
            futureBallPosition = computeBallTrajectory(timeToReachAerial, input);
            futureCarPosition = carPredictedTrajectory.compute(timeToReachAerial);
            aerialDistanceError = futureBallPosition.minus(futureCarPosition);
            deltaOrientationToConverge = aerialDistanceError.scaled(1/input.car.position.minus(futureBallPosition).magnitude());
            naiveOrientationGuess = naiveOrientationGuess.plus(deltaOrientationToConverge).normalized();
        }

        naiveOrientationGuess = naiveOrientationGuess.plus(input.car.velocity.scaledToMagnitude(-futureBallPosition.minus(futureCarPosition).magnitude()/1000)).normalized();

        return naiveOrientationGuess;
    }

    private Vector3 computeBallTrajectory(double time, DataPacket input) {
        Vector3 futureBallPosition = input.statePrediction.ballAtTime(time).position;
        Vector3 initialOffset = futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS);

        return futureBallPosition.plus(initialOffset);
    }

    private void updateCarPredictedTrajectory(Vector3 naiveOrientationGuess, DataPacket input) {
        if(!input.car.isSupersonic) {
            carPredictedTrajectory = new Parabola3D(
                    input.car.position,
                    input.car.velocity,
                    new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH).plus(naiveOrientationGuess.scaledToMagnitude(RlConstants.ACCELERATION_DUE_TO_BOOST)),
                    0
            );
        }
        else {
            Vector3 acceleration = new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH).plus(naiveOrientationGuess.scaledToMagnitude(RlConstants.ACCELERATION_DUE_TO_BOOST));
            carPredictedTrajectory = new Parabola3D(
                    input.car.position,
                    input.car.velocity,
                    acceleration.minus(acceleration.projectOnto(input.car.velocity)),
                    0
            );
        }
    }

    private double findTimeOfClosestApproachToBall(DataPacket input) {
        Trajectory3D ballTrajectory = new Trajectory3D() {
            @Override
            public Vector3 compute(double time) {
                return computeBallTrajectory(time, input);
            }
        };

        return Trajectory3D.findTimeOfClosestApproachBetween(ballTrajectory, carPredictedTrajectory,
                RlUtils.BALL_PREDICTION_TIME, RlUtils.BALL_PREDICTION_REFRESH_RATE);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position.toFlatVector(), input.car.position.plus(orientation.scaled(300)).toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(ballDestination, Color.MAGENTA);
        shapeRenderer.renderCross(futureCarPosition, Color.red);
        shapeRenderer.renderTrajectory(carPredictedTrajectory, -0.5, 0, Color.pink);
        shapeRenderer.renderTrajectory(carPredictedTrajectory, 0, 3, Color.CYAN);
        //shapeRenderer.renderHitBox(futureHitBox, Color.YELLOW);
    }
}
