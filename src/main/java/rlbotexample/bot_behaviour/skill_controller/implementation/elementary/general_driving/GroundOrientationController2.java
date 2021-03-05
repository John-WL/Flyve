package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GroundOrientationController2 extends SkillController {

    private final BotBehaviour bot;
    private GroundSpinController groundSpinController;

    private Vector3 currentNoseDestination;

    public GroundOrientationController2(BotBehaviour bot) {
        this.bot = bot;
        this.groundSpinController = new GroundSpinController(bot);

        this.currentNoseDestination = new Vector3();
    }

    public void setDestination(final Vector3 noseDestination) {
        this.currentNoseDestination = noseDestination;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 localNoseDestination = currentNoseDestination
                .minus(input.car.position)
                .toFrameOfReference(input.car.orientation);
        double correctionAngle = localNoseDestination
                .flatten()
                .correctionAngle(Vector2.X_VECTOR)
                * 6;

        groundSpinController.setSpin(correctionAngle);
        groundSpinController.updateOutput(input);
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(currentNoseDestination, Color.CYAN);
        renderer.drawLine3d(Color.CYAN,
                input.car.position.plus(input.car.orientation.noseVector.scaled(4000).plus(new Vector3(0, 0, 100)))
                        .toFlatVector(),
                input.car.position
                        .toFlatVector());
    }
}
