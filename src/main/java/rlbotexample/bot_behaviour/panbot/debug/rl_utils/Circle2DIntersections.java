package rlbotexample.bot_behaviour.panbot.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle;

import java.awt.*;

public class Circle2DIntersections extends FlyveBot {

    private Circle circle1;
    private Circle circle2;

    public Circle2DIntersections() {
        this.circle1 = new Circle(new Vector2(0, 0), -500);
        this.circle2 = new Circle(new Vector2(-500, 0), 500);
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCircle(circle1, 50, Color.blue);
        shapeRenderer.renderCircle(circle2, 50, Color.blue);
        Vector2[] intersectionPoints = circle1.findIntersectionPoints(circle2);
        shapeRenderer.renderCross(new Vector3(intersectionPoints[0], 50), Color.GREEN);
        shapeRenderer.renderCross(new Vector3(intersectionPoints[1], 50), Color.GREEN);
    }
}
