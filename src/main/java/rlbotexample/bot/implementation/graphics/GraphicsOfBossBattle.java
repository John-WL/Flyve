package rlbotexample.bot.implementation.graphics;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.CarOrientedPosition;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GraphicsOfBossBattle {

    public static void print(DataPacket input, Renderer renderer) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        //shapeRenderer.renderCross(input.car.position.plus(new Vector3(-33, 0, 22)), Color.cyan);

        //shapeRenderer.renderOrientedPosition(Color.cyan, new CarOrientedPosition(input.humanCar.position, input.humanCar.orientation));
        shapeRenderer.renderOrientedPosition(Color.cyan, new CarOrientedPosition(input.humanCar.position, input.humanCar.orientation).toZyxOrientedPosition());
    }
}