package rlbotexample.bot_behaviour.flyve.debug.rl_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.ground.GroundTrajectory2DInfo;
import rlbotexample.input.dynamic_data.ground.GroundTrajectoryFinder2;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class GroundTrajectory2DPrinting extends FlyveBot {

    public GroundTrajectory2DInfo groundTrajectory;

    public GroundTrajectory2DPrinting() {
        this.groundTrajectory = null;
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ExtendedCarData car = input.car;
        Vector3 ballDestination = new Vector3(0, 5200, 100);
        groundTrajectory = new GroundTrajectoryFinder2(new Trajectory3D() {
                    @Override
                    public Vector3 compute(double time) {
                        return input.statePrediction.ballAtTime(time).position;
                    }
                },
                new Trajectory3D() {
                    @Override
                    public Vector3 compute(double time) {
                        Vector3 ballPosition = input.statePrediction.ballAtTime(time).position;
                        Vector3 offset = ballDestination.minus(ballPosition).normalized();
                        return offset;
                    }
                }).findGroundTrajectory2DInfo(car);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderGroundTrajectory2D(groundTrajectory,
                50,
                Color.yellow);
    }
}
