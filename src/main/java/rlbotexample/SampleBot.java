package rlbotexample;

import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbot.flat.GameTickPacket;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.PanBot;
import rlbotexample.input.boost.BoostManager;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import rlbotexample.output.ControlsOutput;

import java.awt.*;

public class SampleBot implements Bot {

    private final int playerIndex;
    private BotOutput myBotOutput;
    private PanBot myBotBehaviour;


    public SampleBot(int playerIndex) {
        this.playerIndex = playerIndex;
        myBotOutput = new BotOutput();
        myBotBehaviour = new PanBot();
    }

    /**
     * This is where we keep the actual bot logic. This function shows how to chase the ball.
     * Modify it to make your bot smarter!
     */
    private ControlsOutput processInput(DataPacket input, GameTickPacket packet) {

        // Bot behaviour
        myBotOutput = myBotBehaviour.processInput(input, packet);

        // debug
        Renderer renderer = getRenderer();
        myBotBehaviour.displayDebugLines(renderer, input);

        // Output the calculated states
        return myBotOutput.getOutput();
    }

    /**
     * This is a nice example of using the rendering feature.
     */
    private void drawDebugLines(DataPacket input, CarData myCar, boolean goLeft) {

        // Here's an example of rendering debug data on the screen.
        Renderer renderer = BotLoopRenderer.forBotLoop(this);

        /*
        // Draw a line from the car to the ball
        renderer.drawLine3d(Color.LIGHT_GRAY, myCar.position, input.ball.position);

        // Draw a line that points out from the nose of the car.
        renderer.drawLine3d(goLeft ? Color.BLUE : Color.RED,
                myCar.position.plus(myCar.orientation.noseVector.scaled(150)),
                myCar.position.plus(myCar.orientation.noseVector.scaled(300)));

        renderer.drawString3d(goLeft ? "left" : "right", Color.WHITE, myCar.position, 2, 2);


        try {
            // Draw 3 seconds of ball prediction
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            BallPredictionHelper.drawTillMoment(ballPrediction, myCar.elapsedSeconds + 3, Color.CYAN, renderer);
        } catch (RLBotInterfaceException e) {
            e.printStackTrace();
        }
        */
    }

    private Renderer getRenderer() {
        return BotLoopRenderer.forBotLoop(this);
    }


    @Override
    public int getIndex() {
        return this.playerIndex;
    }

    /**
     * This is the most important function. It will automatically get called by the framework with fresh data
     * every frame. Respond with appropriate controls!
     */
    @Override
    public ControllerState processInput(GameTickPacket packet) {

        if (packet.playersLength() <= playerIndex || packet.ball() == null || !packet.gameInfo().isRoundActive()) {
            // Just return immediately if something looks wrong with the data. This helps us avoid stack traces.
            return new ControlsOutput();
        }

        // Update the boost manager and tile manager with the latest data
        BoostManager.loadGameTickPacket(packet);

        // Translate the raw packet data (which is in an unpleasant format) into our custom DataPacket class.
        // The DataPacket might not include everything from GameTickPacket, so improve it if you need to!
        DataPacket dataPacket = new DataPacket(packet, playerIndex);

        // Do the actual logic using our dataPacket.
        ControlsOutput controlsOutput = processInput(dataPacket, packet);

        return controlsOutput;
    }

    public void retire() {
        System.out.println("Retiring pan bot " + playerIndex);
    }
}
