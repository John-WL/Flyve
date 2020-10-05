package rlbotexample.bot_behaviour.panbot;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense.Dribble;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.path.EnemyNetPositionPath;
import rlbotexample.bot_behaviour.path.PathHandler;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.debug.BezierDebugger;
import util.vector.Vector3;

import java.awt.*;

public class Normal1sV0 extends PanBot {

    private CarDestination desiredDestination;
    private SkillController skillController;
    private PathHandler pathHandler;

    public Normal1sV0() {
        desiredDestination = new CarDestination();
        skillController = new Dribble(desiredDestination, this);
        pathHandler = new EnemyNetPositionPath(desiredDestination);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // calculate next desired destination
        pathHandler.updateDestination(input);

        // do the thing
        skillController.setupAndUpdateOutputs(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        Vector3 playerPosition = input.car.position;
        Vector3 destination = desiredDestination.getThrottleDestination();
        Vector3 steeringPosition = desiredDestination.getSteeringDestination();

        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.LIGHT_GRAY, playerPosition, destination);
        renderer.drawLine3d(Color.MAGENTA, playerPosition, steeringPosition);
        BezierDebugger.renderPath(desiredDestination.getPath(), Color.blue, renderer);
    }
}
