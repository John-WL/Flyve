package rlbotexample.bot_behaviour.panbot.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray2;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle;

import java.awt.*;

public class Circle2DTangent extends FlyveBot {

    private Circle circle1;
    private Vector2 pointOnTangent;

    public Circle2DTangent() {
        this.circle1 = new Circle(new Vector2(0, 0), 500);
        this.pointOnTangent = new Vector2(900, 1200);
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
        Ray2[] tangentPoints = circle1.findTangentsFrom(pointOnTangent);
        shapeRenderer.renderCross(new Vector3(tangentPoints[0].offset, 50), Color.GREEN);
        shapeRenderer.renderCross(new Vector3(tangentPoints[1].offset, 50), Color.GREEN);
        shapeRenderer.renderCross(new Vector3(pointOnTangent, 50), Color.red);
        renderer.drawLine3d(Color.magenta, new Vector3(pointOnTangent, 50), new Vector3(tangentPoints[0].offset, 50));
        renderer.drawLine3d(Color.magenta, new Vector3(pointOnTangent, 50), new Vector3(tangentPoints[1].offset, 50));
    }
}
