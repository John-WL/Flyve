package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Flip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.MiddleJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.Wait;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GroundDirectionalHit5 extends SkillController {

    private BotBehaviour botBehaviour;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    private Vector3 ballDestination;
    private Vector3 accelerationImpulse;

    public GroundDirectionalHit5(BotBehaviour bot) {
        this.botBehaviour = bot;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);

        this.ballDestination = new Vector3();
        this.accelerationImpulse = new Vector3();
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 vf = ballDestination.minus(input.ball.position).scaledToMagnitude(input.ball.velocity.magnitude());
        Vector3 vi = input.ball.velocity;
        accelerationImpulse = vf.minus(vi);

        drivingSpeedController.setSpeed(1440);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(input.ball.position.plus(accelerationImpulse.scaledToMagnitude(-RlConstants.BALL_RADIUS)));
        groundOrientationController.updateOutput(input);
    }

    private double ballApogeeHeight(BallData ballData) {
        double timeFromApogee = (ballData.velocity.z/RlConstants.NORMAL_GRAVITY_STRENGTH);
        return ballData.position.z - RlConstants.BALL_RADIUS
                + ballData.velocity.z*timeFromApogee
                - 0.5 * RlConstants.NORMAL_GRAVITY_STRENGTH * timeFromApogee * timeFromApogee;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.MAGENTA, input.ball.position.toFlatVector(), input.ball.position.plus(ballDestination.minus(input.ball.position).scaledToMagnitude(input.ball.velocity.magnitude())).toFlatVector());
        renderer.drawLine3d(Color.blue, input.ball.position.toFlatVector(), input.ball.position.plus(input.ball.velocity).toFlatVector());
        renderer.drawLine3d(Color.yellow, input.ball.position.toFlatVector(), input.ball.position.plus(accelerationImpulse).toFlatVector());

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(new Vector3(input.ball.position.flatten(), ballApogeeHeight(input.ball)), Color.red);
    }
}
