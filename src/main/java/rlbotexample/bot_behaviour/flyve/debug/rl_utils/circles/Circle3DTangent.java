package rlbotexample.bot_behaviour.flyve.debug.rl_utils.circles;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray2;
import util.math.vector.Ray3;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle;
import util.shapes.Circle3D;

import java.awt.*;

public class Circle3DTangent extends FlyveBot {

    private Circle3D circle1;
    private Vector3 pointOnTangent;

    public Circle3DTangent() {
        this.circle1 = new Circle3D(new Ray3(new Vector3(300, -1000, 400), new Vector3(0, 0, -1)), 500);
        this.pointOnTangent = new Vector3(1500, -2000, 100);
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        pointOnTangent = input.allCars.get(1-input.playerIndex).position;
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCircle3D(circle1, Color.blue);
        Ray3 tangent = circle1.findTangentPointFrom(pointOnTangent, 0);
        shapeRenderer.renderCross(pointOnTangent, Color.MAGENTA);
        shapeRenderer.renderCross(tangent.offset, Color.GREEN);
        shapeRenderer.renderRay3(tangent, Color.GREEN);
    }
}
