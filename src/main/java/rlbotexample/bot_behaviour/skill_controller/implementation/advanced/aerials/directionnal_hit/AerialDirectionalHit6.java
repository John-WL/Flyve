package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.aerials.AerialUtils;
import rlbotexample.input.dynamic_data.car.HitBox;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class AerialDirectionalHit6 extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController2 aerialOrientationHandler;
    private JumpController jumpController;

    private Vector3 ballDestination;
    private AerialTrajectoryInfo aerialInfo;
    private Parabola3D carPredictedTrajectory;
    private double timeToReachAerial;
    private Vector3 futureBallPosition;
    private Vector3 futureCarPosition;
    private HitBox futureHitBox;

    public AerialDirectionalHit6(BotBehaviour bot) {
        this.bot = bot;
        this.aerialOrientationHandler = new AerialOrientationController2(bot);
        this.jumpController = new JumpController(bot);

        this.aerialInfo = new AerialTrajectoryInfo();
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

        aerialInfo = findAerialTrajectoryInfo(input);
        Vector3 orientation = aerialInfo.acceleration;

        // orientation handling
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

    private AerialTrajectoryInfo findAerialTrajectoryInfo(DataPacket input) {
        AerialTrajectoryInfo aerialInfo = AerialUtils.findAerialTrajectoryInfo(input.car, new Trajectory3D() {
            @Override
            public Vector3 compute(double time) {
                Vector3 futureBallPosition = input.statePrediction.ballAtTime(time).position;
                return futureBallPosition.plus(futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS));
            }
        });
        carPredictedTrajectory = new Parabola3D(
                input.car.position,
                input.car.velocity,
                aerialInfo.acceleration.minus(new Vector3(0, 0, RlConstants.NORMAL_GRAVITY_STRENGTH)),
                0
        );

        // very weird way to converge....
        // there might be a better way, like taking in consideration the hit-box, first...
        Vector3 velocityAtImpact = carPredictedTrajectory.derivative(aerialInfo.timeOfFlight).scaledToMagnitude(RlConstants.ACCELERATION_DUE_TO_BOOST/2);
        Vector3 futureBallPosition = input.statePrediction.ballAtTime(aerialInfo.timeOfFlight).position;
        Vector3 destinationOffset = ballDestination.minus(futureBallPosition).scaledToMagnitude(velocityAtImpact.magnitude());

        aerialInfo.acceleration = aerialInfo.acceleration.plus(velocityAtImpact.minus(destinationOffset).scaled(1, 1, 0));

        return aerialInfo;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position, input.car.position.plus(aerialInfo.acceleration.scaledToMagnitude(300)));
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(ballDestination, Color.MAGENTA);
        shapeRenderer.renderCross(futureCarPosition, Color.red);
        shapeRenderer.renderParabola3D(carPredictedTrajectory, -0.5, 0, Color.pink);
        shapeRenderer.renderParabola3D(carPredictedTrajectory, 0, 3, Color.CYAN);
        //shapeRenderer.renderHitBox(futureHitBox, Color.YELLOW);
    }
}
