package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.directionnal_hit.parabola;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.BoostController;
import util.game_constants.RlConstants;
import util.renderers.ShapeRenderer;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialDirectionalHit2 extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController2 aerialOrientationHandler;
    private JumpController jumpController;
    private BoostController boostController;
    private Vector3 ballDestination;
    private Vector3 playerDestination;
    private Vector3 futureBallPosition;
    private Vector3 orientation;
    private double timeToReachAerial;

    public AerialDirectionalHit2(BotBehaviour bot) {
        this.bot = bot;
        this.aerialOrientationHandler = new AerialOrientationController2(bot);
        this.jumpController = new JumpController(bot);

        this.orientation = new Vector3();
        this.ballDestination = new Vector3();
        this.playerDestination = new Vector3();
        this.futureBallPosition = new Vector3();
        this.timeToReachAerial = 0;
    }

    public void setBallDestination(Vector3 destination) {
        this.ballDestination = destination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 playerPosition = input.car.position;

        playerDestination = input.statePrediction.ballAtTime(0).position;
        // uh... try to binary search the right ball I guess?
        Vector3 orientationWithoutGravity = new Vector3();
        for(int i = 0; i < 100; i++) {
            Vector3 unscaledOrientationWithGravity = playerDestination.minus(input.car.velocity.plus(input.car.velocity.projectOnto(input.car.position.minus(playerDestination)).scaled(-1)).scaled(10))
                    .minus(input.car.position);
            //Vector3 unscaledOrientationWithGravity = playerDestination.minus(input.car.position).normalized();
            // fancy math to retrieve the desired orientation of player from the applied acceleration vector, without gravity
            //double angleFromZCoordinate = Math.atan(globalOrientationWithGravity.minus(input.car.position).z / globalOrientationWithGravity.minus(input.car.position).flatten().magnitude());
            double angleFromZCoordinate = Math.atan(unscaledOrientationWithGravity.z / unscaledOrientationWithGravity.flatten().magnitude());
            double lengthOfVector = RlConstants.NORMAL_GRAVITY_STRENGTH + ((1 - Math.cos(angleFromZCoordinate)) * (RlConstants.ACCELERATION_DUE_TO_BOOST - RlConstants.NORMAL_GRAVITY_STRENGTH));
            Vector3 orientationWithGravity = unscaledOrientationWithGravity.scaledToMagnitude(lengthOfVector);
            orientationWithoutGravity = orientationWithGravity.minus(new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH));
            orientation = orientationWithoutGravity
            //        .minus(input.car.velocity.plus(input.car.velocity.projectOnto(input.car.position.minus(playerDestination)).scaled(-1)).scaled(3))
                    .plus(input.car.position);

            // try to calculate the time it'll take to reach the destination?
            double a = input.car.orientation.noseVector.scaled(RlConstants.ACCELERATION_DUE_TO_BOOST)
                    .plus(Vector3.UP_VECTOR.scaled(RlConstants.NORMAL_GRAVITY_STRENGTH))
                    .projectOnto(input.car.position.minus(playerDestination)).magnitude()/2;
            double b = input.car.velocity.projectOnto(input.car.position.minus(playerDestination)).magnitude();
            double c = -input.car.position.minus(playerDestination).magnitude();
            timeToReachAerial = (-b + Math.sqrt(b*b - 4*a*c))
                                                    / (2*a);
            //System.out.println(timeToReachDestination);

            futureBallPosition = input.statePrediction.ballAtTime(timeToReachAerial).position;

            Vector3 offset = futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS);
            playerDestination = futureBallPosition.plus(offset);
        }

        //output.boost(input.car.orientation.noseVector.dotProduct(orientationWithoutGravity.normalized()) > 0.7);
        output.boost(boostController.process(input.car.orientation.noseVector.dotProduct(orientation.minus(input.car.position).normalized())*1.3));

        // set the desired orientation and apply it
        aerialOrientationHandler.setOrientationDestination(orientation);
        if(timeToReachAerial < 1.5) {
            aerialOrientationHandler.setRollOrientation(input.ball.position);
        }
        else {
            aerialOrientationHandler.setRollOrientation(input.car.position.plus(input.car.orientation.roofVector));
        }
        aerialOrientationHandler.updateOutput(input);

        jumpController.setFirstJumpType(new SimpleJump(), input);
        jumpController.setSecondJumpType(new ShortJump(), input);
        jumpController.updateOutput(input);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position, orientation);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(playerDestination, Color.red);
        shapeRenderer.renderCross(ballDestination, Color.MAGENTA);
        renderer.drawLine3d(Color.cyan, playerDestination, futureBallPosition);
    }
}
