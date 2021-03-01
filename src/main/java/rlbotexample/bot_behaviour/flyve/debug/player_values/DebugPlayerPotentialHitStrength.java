package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.metagame.advanced_gamestate_info.OffensiveStateInfo;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class DebugPlayerPotentialHitStrength extends FlyveBot {
    public DebugPlayerPotentialHitStrength() {

    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderCross(OffensiveStateInfo.potentialHitStrength(input, 1-input.playerIndex).plus(input.ball.position), Color.red);

        Vector2 hitStrength = OffensiveStateInfo.potentialHitStrength(input, 1-input.playerIndex).flatten();

        renderer.drawLine3d(Color.red, OffensiveStateInfo.potentialHitStrength(input, 1-input.playerIndex).plus(input.ball.position).toFlatVector(), input.ball.position.toFlatVector());
        if(hitStrength.magnitude() > 1300) {
            renderer.drawString3d("hook shot", Color.YELLOW, input.allCars.get(1-input.playerIndex).position.toFlatVector(), 2, 2);
        }
        else if(hitStrength.magnitude() < 300) {
            //renderer.drawString3d("dribble", Color.YELLOW, input.allCars.get(1-input.playerIndex).position.toFlatVector(), 1, 1);
            renderer.drawString3d("bounce dribble", Color.blue, input.allCars.get(1-input.playerIndex).position.toFlatVector(), 2, 2);
        }
        else {
            renderer.drawString3d("bounce dribble", Color.blue, input.allCars.get(1-input.playerIndex).position.toFlatVector(), 2, 2);
        }
    }
}
