package rlbotexample.bot_behaviour.flyve.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.geometry.MapMeshGeometry;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray3;
import util.shapes.Sphere;

import java.awt.*;

public class DebugPlayerDistanceFromMap extends FlyveBot {

    public DebugPlayerDistanceFromMap() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        System.out.println(StandardMapSplitMesh.STANDARD_MAP_MESH.collideWith(new Sphere(input.allCars.get(1-input.playerIndex).position, 1100)).direction
                .minus(input.allCars.get(1-input.playerIndex).position));
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        Ray3 collision = StandardMapSplitMesh.STANDARD_MAP_MESH.collideWith(new Sphere(input.allCars.get(1-input.playerIndex).position, 1100));
        renderer.drawLine3d(
                Color.CYAN,
                collision.direction.plus(collision.offset).toFlatVector(),
                input.allCars.get(1-input.playerIndex).position.toFlatVector());
    }
}
