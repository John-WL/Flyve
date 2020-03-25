package rlbotexample.bot_behaviour;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.bot_movements.MovementOutputHandler;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.input.dynamic_data.DataPacket;
import util.debug.BezierDebugger;
import util.vector.Vector3;

import java.awt.*;

public class PanBotFpsLogger {

    private CarDestination desiredDestination;
    private MovementOutputHandler movementOutputHandler;

    public PanBotFpsLogger(MovementOutputHandler movementOutputHandler, CarDestination desiredDestination) {
        this.movementOutputHandler = movementOutputHandler;
        this.desiredDestination = desiredDestination;
    }

    public void displayDebugLines(Renderer renderer, DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 throttleDestination = desiredDestination.getThrottleDestination();
        Vector3 steeringDestination = desiredDestination.getSteeringDestination(input);
        Vector3 aerialDestination = desiredDestination.getAerialDestination();
        boolean isAerialing = movementOutputHandler.isAerialing();

        renderer.drawLine3d(Color.LIGHT_GRAY, myPosition, throttleDestination);
        renderer.drawLine3d(Color.MAGENTA, myPosition, steeringDestination);
        if(isAerialing) {
            renderer.drawLine3d(Color.green, myPosition, aerialDestination);
        }
        else {
            renderer.drawLine3d(Color.ORANGE, myPosition, aerialDestination);

        }
        BezierDebugger.renderPath(desiredDestination.getPath(), Color.blue, renderer);
        //BezierDebugger.renderPositionControlledByCar(input, Color.PINK, renderer);
    }

    public void displayFpsCounter(Renderer renderer, double fps) {
        if(Math.abs(fps - 30) < 5) {
            renderer.drawString2d(String.format("%.4f", fps), Color.green, new Point(10, 10), 2, 2);
        }
        else if(Math.abs(fps - 30) < 7) {
            renderer.drawString2d(String.format("%.4f", fps), Color.yellow, new Point(10, 10), 2, 2);
        }
        else {
            renderer.drawString2d(String.format("%.4f", fps), Color.red, new Point(10, 10), 2, 2);
        }
    }

    public void displayAvgFps(Renderer renderer, double fps) {
        if(Math.abs(fps - 30) < 5) {
            renderer.drawString2d(String.format("%.4f", fps) + "", Color.green, new Point(10, 50), 2, 2);
        }
        else if(Math.abs(fps - 30) < 7) {
            renderer.drawString2d(String.format("%.4f", fps) + "", Color.yellow, new Point(10, 50), 2, 2);
        }
        else {
            renderer.drawString2d(String.format("%.4f", fps) + "", Color.red, new Point(10, 50), 2, 2);
        }
    }

    public void displayMsPerFrame(Renderer renderer, long msPerFrame) {
        if(msPerFrame < 15) {
            renderer.drawString2d(msPerFrame + "", Color.green, new Point(10, 90), 2, 2);
        }
        else if(msPerFrame < 33) {
            renderer.drawString2d(msPerFrame + "", Color.yellow, new Point(10, 90), 2, 2);
        }
        else {
            renderer.drawString2d(msPerFrame + "", Color.red, new Point(10, 90), 2, 2);
        }
    }
}
