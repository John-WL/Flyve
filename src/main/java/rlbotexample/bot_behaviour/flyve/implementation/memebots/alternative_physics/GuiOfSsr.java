package rlbotexample.bot_behaviour.flyve.implementation.memebots.alternative_physics;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import util.game_constants.RlConstants;
import util.renderers.ShapeRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiOfSsr {

    private static final List<AssignedVector3> assignedImpulses = new ArrayList<>();
    private static final List<AssignedVector3> assignedPenetrations = new ArrayList<>();

    public static void print(DataPacket input, Renderer renderer) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        for(ExtendedCarData car: input.allCars) {
            if (car.isInBallForm) {
                shapeRenderer.renderSwerlingSphere(car.position, PhysicsOfSsr.PLAYERS_RADII, Color.CYAN);
            }
            else {
                shapeRenderer.renderSwerlingSphere(car.position, PhysicsOfSsr.PLAYERS_RADII, Color.orange);
            }
        }
    }
}
