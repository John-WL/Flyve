package rlbotexample.bot_behaviour.flyve.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle3D;

import java.awt.*;

public class RenderingCircle3DToTestIt extends FlyveBot {

    private Circle3D circle;
    private Ray3 center;

    public RenderingCircle3DToTestIt() {
        this.center = new Ray3(new Vector3(0, 0, 200), new Vector3(1, 0, 1).scaledToMagnitude(100));
        this.circle = new Circle3D(center, 300);
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.cyan, center.offset, center.offset.plus(center.direction));
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCircle3D(circle, Color.green);
        shapeRenderer.renderCross(center.offset, Color.magenta);
        shapeRenderer.renderCross(circle.findClosestPointFrom(input.allCars.get(1-input.playerIndex).position), Color.red);
        renderer.drawLine3d(Color.ORANGE, circle.findPointOnCircle(circle.findRadsFromClosestPoint(input.allCars.get(1-input.playerIndex).position)), input.allCars.get(1-input.playerIndex).position);
    }
}
