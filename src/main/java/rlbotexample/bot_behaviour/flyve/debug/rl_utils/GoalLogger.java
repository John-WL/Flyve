package rlbotexample.bot_behaviour.flyve.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.goal.GoalRegion;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle;

import java.awt.*;

public class GoalLogger extends FlyveBot {

    public GoalLogger() {

    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);

        renderer.drawLine3d(Color.GREEN, input.allCars.get(1-input.playerIndex).position, StandardMapGoals.getOpponent(input.team).closestPointOnSurface(input.allCars.get(1-input.playerIndex).position));
        shapeRenderer.renderCross(StandardMapGoals.getOpponent(input.team).closestPointOnSurface(input.allCars.get(1-input.playerIndex).position), Color.MAGENTA);
    }
}
