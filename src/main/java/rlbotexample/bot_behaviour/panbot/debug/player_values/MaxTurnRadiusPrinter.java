package rlbotexample.bot_behaviour.panbot.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.MaxTurnRadiusFinder;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle;

import java.awt.*;

public class MaxTurnRadiusPrinter extends PanBot {

    private Circle rightTurn;
    private Circle leftTurn;

    public MaxTurnRadiusPrinter() {
        super();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        double radii = MaxTurnRadiusFinder.compute(input.allCars.get(1-input.playerIndex).velocity.magnitude());

        Vector2 offset = input.allCars.get(1-input.playerIndex).orientation.rightVector.flatten().scaled(radii);

        rightTurn = new Circle(input.allCars.get(1-input.playerIndex).position.flatten().plus(offset), radii);
        leftTurn = new Circle(input.allCars.get(1-input.playerIndex).position.flatten().minus(offset), radii);

        return new BotOutput();
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer sr = new ShapeRenderer(renderer);

        sr.renderCircle(rightTurn, 50, Color.cyan);
        sr.renderCircle(leftTurn, 50, Color.cyan);
    }
}
