package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.intersect_destination;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationHandler;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.renderers.ShapeRenderer;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialIntersectDestination extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationHandler aerialOrientationHandler;
    private JumpController jumpHandler;
    private Vector3 destination;
    private Vector3 orientation;

    public AerialIntersectDestination(BotBehaviour bot) {
        this.bot = bot;
        this.aerialOrientationHandler = new AerialOrientationHandler(bot);
        this.jumpHandler = new JumpController(bot);

        this.orientation = new Vector3();
        this.destination = new Vector3();
    }

    public void setDestination(Vector3 destination) {
        this.destination = destination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 playerPosition = input.car.position;
        Vector3 playerSpeed = input.car.velocity;
        Vector3 playerDistanceFromDestination = destination.minus(playerPosition);

        // second try
        // this is the player speed SIGNED (it's the player speed, but it's negative if it's going away from the destination...)
        double signedPlayerSpeedFromDestination = playerSpeed.magnitude() *(
                playerSpeed.dotProduct(playerDistanceFromDestination)/(playerDistanceFromDestination.magnitude()*playerSpeed.magnitude())
        );
        // WTF why won't this work with the proper division?? UGH
        double a = -RlConstants.ACCELERATION_DUE_TO_BOOST/1;
        double b = signedPlayerSpeedFromDestination;
        double c = playerDistanceFromDestination.magnitude();
        double timeBeforeReachingDestination = -b - Math.sqrt(b*b - 4*a*c);
        timeBeforeReachingDestination /= 2*a;

        if(timeBeforeReachingDestination > 3) {
            timeBeforeReachingDestination = 3;
        }

        // get the future player position
        //Vector3 playerFuturePosition = predictions.aerialKinematicBody(playerPosition, playerSpeed, timeBeforeReachingDestination).getPosition();
        Vector3 playerFuturePosition = new Parabola3D(playerPosition, playerSpeed, new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH), 0).apply(timeBeforeReachingDestination);

        // get the orientation we should have to hit the getNativeBallPrediction
        Vector3 orientation = destination.minus(playerFuturePosition);

        // update variables so we can print them later in the debugger
        this.orientation = orientation;

        // boost to the destination
        if(input.car.orientation.noseVector.dotProduct(orientation)/orientation.magnitude() > 0.6) {
            output.boost(true);
        }
        else {
            output.boost(false);
        }


        // if we're hitting the getNativeBallPrediction in the future, face the getNativeBallPrediction to hit it properly
        if(destination.minus(playerFuturePosition).magnitude() < 50) {
            output.boost(false);
            orientation = destination.minus(input.car.position);
            this.orientation = orientation;
        }

        // set the desired orientation and apply it
        aerialOrientationHandler.setDestination(orientation.plus(playerPosition));
        aerialOrientationHandler.updateOutput(input);

        // jump to the destination if we're on the ground
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position.toFlatVector(), orientation.plus(input.car.position).toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(destination, Color.red);
    }
}
