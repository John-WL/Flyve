package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class DebugPlayerHitBox extends FlyveBot {

    public DebugPlayerHitBox() {
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        ExtendedCarData playerCar = input.allCars.get(1-input.playerIndex);

        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderHitBox(playerCar.hitBox, Color.YELLOW);
        //shapeRenderer.renderCross(playerCar.hitBox.projectPointOnSurfaceFromCenterOfMass(input.ball.position), Color.CYAN);
        //shapeRenderer.renderCross(playerCar.hitBox.closestPointOnSurface(input.ball.position), Color.red);
        //renderer.drawLine3d(Color.GREEN, input.ball.position, playerCar.position);
    }
}
