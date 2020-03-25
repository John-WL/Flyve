package rlbotexample.bot_behaviour;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.bot_movements.MovementOutputHandler;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.path.PathGenerator;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;

// Pan is an abbreviation for PATCHES ARE NEEDED!
public class PanBot extends BotBehaviour {

    private CarDestination desiredDestination;
    private MovementOutputHandler movementOutputHandler;
    private PanBotFpsLogger botFpsLogger;


    public PanBot() {
        desiredDestination = new CarDestination();
        PathGenerator.dummyPath(desiredDestination);
        movementOutputHandler = new MovementOutputHandler(desiredDestination, this);
        botFpsLogger = new PanBotFpsLogger(movementOutputHandler, desiredDestination);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        // make sure the path is always up to date with the data packet
        PathGenerator.randomGroundPath(desiredDestination, input);

        // bot's desired position advances one step
        desiredDestination.advanceOneStep(input);

        // calculate what output the bot needs to have to reach the just advanced step
        movementOutputHandler.actualizeBotOutput(input);

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        PanBotFpsLogger botFpsLogger = this.botFpsLogger;

        botFpsLogger.displayDebugLines(renderer, input);

        botFpsLogger.displayFpsCounter(renderer, currentFps);
        botFpsLogger.displayAvgFps(renderer, averageFps);
        botFpsLogger.displayMsPerFrame(renderer, botExecutionTime);
    }
}
