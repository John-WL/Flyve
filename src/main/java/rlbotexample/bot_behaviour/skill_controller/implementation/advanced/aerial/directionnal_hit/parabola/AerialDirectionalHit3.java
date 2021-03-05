package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerial.directionnal_hit.parabola;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.ShortJump;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types.SimpleJump;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.renderers.ShapeRenderer;
import util.math.vector.Vector3;

import java.awt.*;

public class AerialDirectionalHit3 extends SkillController {

    private BotBehaviour bot;
    private AerialOrientationController2 aerialOrientationHandler;
    private JumpController jumpHandler;
    private Vector3 ballDestination;
    private Vector3 playerDestination;
    private Vector3 futureBallPosition;
    private Vector3 futurePlayerPosition;
    private Vector3 orientation;
    private double timeToReachAerial;

    public AerialDirectionalHit3(BotBehaviour bot) {
        this.bot = bot;
        this.aerialOrientationHandler = new AerialOrientationController2(bot);
        this.jumpHandler = new JumpController(bot);

        this.orientation = new Vector3();
        this.ballDestination = new Vector3();
        this.playerDestination = new Vector3();
        this.futureBallPosition = new Vector3();
        this.futurePlayerPosition = new Vector3();
        this.timeToReachAerial = 0;
    }

    public void setBallDestination(Vector3 destination) {
        this.ballDestination = destination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        Vector3 playerPosition = input.car.position;

        // try to calculate the time it'll take to reach the destination?
        for(int i = 0; i < 100; i++) {
            double a = input.car.orientation.noseVector.scaled(RlConstants.ACCELERATION_DUE_TO_BOOST_IN_AIR)
                    .plus(Vector3.UP_VECTOR.scaled(RlConstants.NORMAL_GRAVITY_STRENGTH))
                    .projectOnto(input.car.position.minus(playerDestination)).magnitude() / 2;
            double b = input.car.velocity.projectOnto(input.car.position.minus(playerDestination)).magnitude();
            double c = -input.car.position.minus(playerDestination).magnitude();
            timeToReachAerial = (-b + Math.sqrt(b * b - 4 * a * c))
                                            / (2 * a);

            futureBallPosition = input.statePrediction.ballAtTime(timeToReachAerial).position;
            futurePlayerPosition = new Parabola3D(input.car.position, input.car.velocity, Vector3.UP_VECTOR.scaled(-RlConstants.NORMAL_GRAVITY_STRENGTH), 0).apply(timeToReachAerial);

            Vector3 offset = futureBallPosition.minus(ballDestination).scaledToMagnitude(RlConstants.BALL_RADIUS);
            playerDestination = futureBallPosition.plus(offset);
        }

        orientation = playerPosition.plus(playerDestination.minus(futurePlayerPosition));

        output.boost(input.car.orientation.noseVector.dotProduct(orientation.minus(input.car.position).normalized()) > 0.7);
        //output.boost(BoostController.process(input.car.orientation.noseVector.dotProduct(orientation.minus(input.car.position).normalized())*1.3));

        if(futureBallPosition.minus(futurePlayerPosition).magnitude() < 20) {
            //output.boost(false);
        }

        // set the desired orientation and apply it
        aerialOrientationHandler.setOrientationDestination(orientation);
        if(timeToReachAerial < 1.5) {
            aerialOrientationHandler.setRollOrientation(input.ball.position);
        }
        else {
            aerialOrientationHandler.setRollOrientation(input.car.position.plus(input.car.orientation.roofVector));
        }
        aerialOrientationHandler.updateOutput(input);


        // jump to the destination if we're on the ground
        this.jumpHandler.setFirstJumpType(new SimpleJump(), input);
        this.jumpHandler.setSecondJumpType(new ShortJump(), input);
        this.jumpHandler.updateOutput(input);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        renderer.drawLine3d(Color.green, input.car.position.toFlatVector(), orientation.toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(playerDestination, Color.red);
        shapeRenderer.renderCross(ballDestination, Color.MAGENTA);
        renderer.drawLine3d(Color.cyan, playerDestination.toFlatVector(), futureBallPosition.toFlatVector());
    }
}
