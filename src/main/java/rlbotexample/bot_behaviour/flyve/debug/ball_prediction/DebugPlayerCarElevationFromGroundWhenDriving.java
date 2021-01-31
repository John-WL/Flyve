package rlbotexample.bot_behaviour.flyve.debug.ball_prediction;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray3;
import util.math.vector.Vector3;
import util.shapes.Sphere;

import java.awt.*;

public class DebugPlayerCarElevationFromGroundWhenDriving extends FlyveBot {

    public DebugPlayerCarElevationFromGroundWhenDriving() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        System.out.println(input.car.hitBox.closestPointOnSurface(input.car.position.plus(new Vector3(0, 0, 1000))));
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
