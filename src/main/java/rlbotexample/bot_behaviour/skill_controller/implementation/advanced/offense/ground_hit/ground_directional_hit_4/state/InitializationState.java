package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit.ground_directional_hit_4.state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectory2DInfo;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectoryFinder2;
import rlbotexample.input.prediction.Trajectory3D;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.CircleArc;
import util.state_machine.State;

import java.awt.*;

public class InitializationState implements State {

    public GroundTrajectory2DInfo groundTrajectoryInfo;
    public double timeOfTrajectoryGeneration;

    private final BotBehaviour bot;
    private final DrivingSpeedController drivingSpeedController;
    private Trajectory3D destinationTrajectory;
    private Trajectory3D orientationTrajectory;

    public InitializationState(BotBehaviour bot) {
        this.bot = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);

        this.destinationTrajectory = null;
        this.orientationTrajectory = null;

        this.timeOfTrajectoryGeneration = -1;
    }

    public void setDestinationTrajectory(Trajectory3D trajectory) {
        this.destinationTrajectory = trajectory;
    }

    public void setOrientationTrajectory(Trajectory3D trajectory) {
        this.orientationTrajectory = trajectory;
    }

    @Override
    public void exec(DataPacket input) {
        GroundTrajectoryFinder2 groundTrajectoryFinder = new GroundTrajectoryFinder2(destinationTrajectory, orientationTrajectory);
        groundTrajectoryInfo = groundTrajectoryFinder.findGroundTrajectory2DInfo(input.car);
        timeOfTrajectoryGeneration = System.currentTimeMillis()/1000.0;
        CircleArc initialTurn = groundTrajectoryInfo.initialTurn;
        Vector2 rotationCenter = initialTurn.circle.center;
        Vector3 rotationCenter3d = new Vector3(rotationCenter, 0);
        boolean isRightTurn = rotationCenter3d.minus(input.car.position).dotProduct(input.car.orientation.rightVector) > 0;

        drivingSpeedController.setSpeed(1200);
        drivingSpeedController.updateOutput(input);

        //bot.output().steer(isRightTurn ? 1:-1);

        //bot.output().boost(true);
    }

    @Override
    public State next(DataPacket input) {
        return new InitialTurnState(bot, this);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderGroundTrajectory2D(groundTrajectoryInfo, 50, Color.CYAN);
        renderer.drawString3d("init", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
    }
}
