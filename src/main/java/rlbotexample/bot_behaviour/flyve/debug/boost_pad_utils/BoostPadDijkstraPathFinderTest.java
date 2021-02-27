package rlbotexample.bot_behaviour.flyve.debug.boost_pad_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.boost.BoostManager;
import rlbotexample.input.dynamic_data.boost.BoostPad;
import rlbotexample.input.dynamic_data.boost.BoostPadNavigation;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class BoostPadDijkstraPathFinderTest extends FlyveBot {

    public BoostPadDijkstraPathFinderTest() {}

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        Optional<BoostPad> closestFromPlayerOpt = BoostManager.closestActivePadFrom(input.allCars.get(1-input.playerIndex).position);
        Optional<BoostPad> closestFromBallOpt = BoostManager.closestActivePadFrom(input.ball.position);
        closestFromPlayerOpt.ifPresent(closestPadFromPlayer -> closestFromBallOpt.ifPresent(closestPadFromBall -> {
            List<BoostPad> path = BoostPadNavigation.dijkstraPathFinding(closestPadFromPlayer, closestPadFromBall, input);
            for(int i = 1; i < path.size(); i++) {
                renderer.drawLine3d(Color.cyan, path.get(i-1).location.plus(new Vector3(0, 0, 50)).toFlatVector(), path.get(i).location.plus(new Vector3(0, 0, 50)).toFlatVector());
            }
        }));
    }
}
