package rlbotexample.bot_behaviour.flyve.debug.rl_utils.trajectories;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.aerials.AerialTrajectoryInfo;
import rlbotexample.input.dynamic_data.aerials.AerialAccelerationFinder;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Parabola3D;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;

import java.awt.*;

public class ConstantAccelerationToReachAerialDestination extends FlyveBot {

    private Trajectory3D targetTrajectory;
    private AerialAccelerationFinder aerialAccelerationFinder;
    private AerialTrajectoryInfo aerialInfo;
    private Trajectory3D destination;
    private Parabola3D trajectory;

    public ConstantAccelerationToReachAerialDestination() {
        this.targetTrajectory = null;
        this.aerialAccelerationFinder = null;
        this.aerialInfo = new AerialTrajectoryInfo();
        this.destination = RawBallTrajectory.trajectory;
        this.trajectory = null;
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        targetTrajectory = time -> destination.compute(time);

        aerialAccelerationFinder = new AerialAccelerationFinder(targetTrajectory);
        aerialInfo = aerialAccelerationFinder.findAerialTrajectoryInfo(0, input.car);

        trajectory = new Parabola3D(input.allCars.get(1-input.playerIndex).position,
                input.allCars.get(1-input.playerIndex).velocity,
                aerialInfo.acceleration,
                0);

        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.GREEN, input.allCars.get(1-input.playerIndex).position.toFlatVector(), input.allCars.get(1-input.playerIndex).position.plus(aerialInfo.acceleration).toFlatVector());
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(trajectory, 10, Color.CYAN);
        //shapeRenderer.renderCross(destination.compute(0), Color.red);
    }
}
