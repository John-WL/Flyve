package rlbotexample.bot_behaviour.panbot;

import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;

import java.awt.*;

// Pan is an abbreviation for PATCHES ARE NEEDED! (Because patches are needed...)
public abstract class PanBot extends BotBehaviour {

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        displayFpsCounter(renderer, currentFps);
        displayAvgFps(renderer, averageFps);
        displayMsPerFrame(renderer, botExecutionTime);
    }

    private void displayFpsCounter(Renderer renderer, double fps) {
        if(Math.abs(fps - 30) < 3) {
            renderer.drawString2d(String.format("%.4f", fps), Color.green, new Point(10, 10), 2, 2);
        }
        else if(Math.abs(fps - 30) < 5) {
            renderer.drawString2d(String.format("%.4f", fps), Color.yellow, new Point(10, 10), 2, 2);
        }
        else {
            renderer.drawString2d(String.format("%.4f", fps), Color.red, new Point(10, 10), 2, 2);
        }
    }

    private void displayAvgFps(Renderer renderer, double fps) {
        if(Math.abs(fps - 30) < 3) {
            renderer.drawString2d(String.format("%.4f", fps) + "", Color.green, new Point(10, 50), 2, 2);
        }
        else if(Math.abs(fps - 30) < 5) {
            renderer.drawString2d(String.format("%.4f", fps) + "", Color.yellow, new Point(10, 50), 2, 2);
        }
        else {
            renderer.drawString2d(String.format("%.4f", fps) + "", Color.red, new Point(10, 50), 2, 2);
        }
    }

    private void displayMsPerFrame(Renderer renderer, long msPerFrame) {
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
