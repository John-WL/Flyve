package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.dribble;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController5;
import rlbotexample.input.dynamic_data.DataPacket;
import util.controllers.BoostController;
import util.controllers.PidController;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class AirDribble3 extends SkillController {

    private BotBehaviour botBehaviour;
    private Vector3 ballDestination;

    private AerialOrientationController5 aerialOrientationHandler;
    private BoostController boostController;

    private PidController orientationPid = new PidController(0.5, 0, 1);

    public AirDribble3(BotBehaviour bot) {
        this.botBehaviour = bot;
        this.ballDestination = new Vector3();

        this.aerialOrientationHandler = new AerialOrientationController5(bot);

        this.boostController = new BoostController();
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 noseOrientation = findNoseOrientation(input);

        aerialOrientationHandler.setNoseOrientation(noseOrientation);
        aerialOrientationHandler.setRollOrientation(new Vector3(0, 1, 0));
        aerialOrientationHandler.updateOutput(input);

        //boolean isBoosting = findOptimalBoostValue(input);
        boolean isBoosting = boostController.process(130 +
                ballDestination.minus(input.ball.position).z
                - input.ball.velocity.z/1.5);
        botBehaviour.output().boost(isBoosting);
    }

    private Vector3 findNoseOrientation(DataPacket input) {
        Vector3 baseOrientation = input.ball.position.minus(input.car.position).normalized();
        Vector3 desiredOrientation = Vector3.UP_VECTOR;
        Vector3 orientationRotator = baseOrientation.findRotator(desiredOrientation).scaled(-1);

        double rotatorLength = orientationPid.process(orientationRotator.magnitude(), 0);
        orientationRotator = orientationRotator.scaledToMagnitude(rotatorLength);

        if(orientationRotator.magnitude() > Math.PI/10) {
            orientationRotator = orientationRotator.scaledToMagnitude(Math.PI/8);
        }

        return baseOrientation.rotate(orientationRotator);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(ballDestination, Color.red);

        aerialOrientationHandler.debug(renderer, input);
        shapeRenderer.renderHitBox(input.car.hitBox, Color.YELLOW);
    }
}
