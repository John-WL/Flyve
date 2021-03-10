package rlbotexample.bot.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.output.BotOutput;
import util.renderers.ShapeRenderer;

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

        StandardMapGoals.getAlly(input.team).closestPointOfBallOnSurface(input.ball.position.scaled(1, 1, 1)).ifPresent(v ->
                renderer.drawLine3d(Color.GREEN, input.ball.position.toFlatVector(), v.toFlatVector()));
        StandardMapGoals.getAlly(input.team).closestPointOfBallOnSurface(input.ball.position.scaled(1, 1, 1)).ifPresent(v ->
                shapeRenderer.renderCross(v, Color.MAGENTA));
    }
}
