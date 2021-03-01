package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ball.BallData;
import util.game_constants.RlConstants;
import util.math.linear_transform.LinearApproximator;
import util.math.vector.MovingPoint;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class BounceDribble extends SkillController {

    private BotBehaviour botBehaviour;
    private Vector3 carDestination;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    private LinearApproximator speedToDistanceFunction = new LinearApproximator();
    {
        speedToDistanceFunction.sample(new Vector2(-800, 0));
        speedToDistanceFunction.sample(new Vector2(-0.00001, 100));
        speedToDistanceFunction.sample(new Vector2(0, -100));
        speedToDistanceFunction.sample(new Vector2(800, 0));
    }

    public BounceDribble(BotBehaviour bot) {
        botBehaviour = bot;
        carDestination = new Vector3();
        drivingSpeedController = new DrivingSpeedController(bot);
        groundOrientationController = new GroundOrientationController(bot);
    }

    @Override
    public void updateOutput(DataPacket input) {
        double realTimeBallOffset = /*(input.ball.position.z-RlConstants.BALL_RADIUS)*/
                + speedToDistanceFunction.compute(input.ball.velocity.z)//*input.ball.velocity.z
                + RlConstants.BALL_RADIUS;
        if(realTimeBallOffset > 300) {
            //realTimeBallOffset = 300;
        }

        carDestination =
                input.ball.position
                .plus(input.ball.velocity.scaled(1, 1, 0).normalized()
                        .rotate(Vector3.UP_VECTOR.scaled(Math.PI/2))
                        .scaled(realTimeBallOffset));

        drivingSpeedController.setSpeed(input.ball.velocity.magnitude()
                - input.car.position.minus(carDestination)
                        .dotProduct(input.car.orientation.noseVector)*100);
        drivingSpeedController.updateOutput(input);

        groundOrientationController.setDestination(carDestination
                .plus(input.ball.velocity.scaledToMagnitude(200)));
        groundOrientationController.updateOutput(input);
    }

    private double makeNonZero(double abs) {
        if(abs == 0) {
            return 0.00001;
        }

        return abs;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.GREEN, carDestination.toFlatVector(), input.ball.position.toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(carDestination, Color.green);
    }
}
