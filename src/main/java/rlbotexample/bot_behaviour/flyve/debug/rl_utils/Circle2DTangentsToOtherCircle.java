package rlbotexample.bot_behaviour.flyve.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray2;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle;

import java.awt.*;

public class Circle2DTangentsToOtherCircle extends FlyveBot {

    private Circle circle1;
    private Circle circle2;

    public Circle2DTangentsToOtherCircle() {
        this.circle1 = new Circle(new Vector2(0, 0), 200);
        this.circle2 = new Circle(new Vector2(-600, 1), 200);
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        this.circle1 = new Circle(input.allCars.get(1-input.playerIndex).position.flatten(), 200);

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCircle(circle1, 50, Color.blue);
        shapeRenderer.renderCircle(circle2, 50, Color.blue);
        Ray2[] tangents = circle1.findTangentsFrom(circle2);
        renderer.drawLine3d(Color.magenta, new Vector3(tangents[0].offset, 50), new Vector3(tangents[0].offset.plus(tangents[0].direction), 50));
        //renderer.drawLine3d(Color.magenta, new Vector3(tangents[1].offset, 50), new Vector3(tangents[1].offset.plus(tangents[1].direction), 50));
        //renderer.drawLine3d(Color.magenta, new Vector3(tangents[2].offset, 50), new Vector3(tangents[2].offset.plus(tangents[2].direction), 50));
        //renderer.drawLine3d(Color.magenta, new Vector3(tangents[3].offset, 50), new Vector3(tangents[3].offset.plus(tangents[3].direction), 50));
    }
}
