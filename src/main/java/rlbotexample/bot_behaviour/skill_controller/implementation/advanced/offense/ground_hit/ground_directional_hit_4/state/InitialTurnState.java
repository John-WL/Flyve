package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit.ground_directional_hit_4.state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectory2DInfo;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.CircleArc;
import util.state_machine.State;

import java.awt.*;

public class InitialTurnState implements State {

    private final BotBehaviour bot;
    private final InitializationState initializationState;

    private final DrivingSpeedController drivingSpeedController;

    public InitialTurnState(BotBehaviour bot, InitializationState initializationState) {
        this.bot = bot;
        this.initializationState = initializationState;

        this.drivingSpeedController = new DrivingSpeedController(bot);
    }

    @Override
    public void exec(DataPacket input) {
        CircleArc initialTurn = initializationState.groundTrajectoryInfo.initialTurn;
        Vector2 rotationCenter = initialTurn.circle.center;
        Vector3 rotationCenter3d = new Vector3(rotationCenter, 0);
        boolean isRightTurn = rotationCenter3d.minus(input.car.position).dotProduct(input.car.orientation.rightVector) > 0;

        drivingSpeedController.setSpeed(1200);
        drivingSpeedController.updateOutput(input);

        bot.output().steer(isRightTurn ? 1:-1);
        bot.output().drift(false);
    }

    @Override
    public State next(DataPacket input) {
        // finding the time elapsed from the trajectory generation (aka since when did we execute the code in the initialization state?)
        double timeElapsedFromTrajectoryGeneration = (System.currentTimeMillis()/1000.0)
                - initializationState.timeOfTrajectoryGeneration;

        // just getting the trajectory reference
        GroundTrajectory2DInfo groundTrajectoryInfo = initializationState.groundTrajectoryInfo;

        // finding the position we're expected to be at
        Vector2 expectedPositionOnTrajectory = groundTrajectoryInfo.findPointFromElapsedTimeAndSpeed(timeElapsedFromTrajectoryGeneration, input.car.velocity.magnitude());

        // if we reached the point where we need to stay in a straight line, just change the state to that one
        if(groundTrajectoryInfo.hasPassedInitialTurn(timeElapsedFromTrajectoryGeneration, input.car.velocity.magnitude())) {
            return new StraightLineState(bot, initializationState);
        }
        // if we "got lost" while turning (aka are we too far away from the expected point?), then re-compute the ground trajectory
        // with the initialization state
        if(expectedPositionOnTrajectory.minus(input.car.position.flatten()).magnitude() > 10) {
            return initializationState;
        }

        // stay in this state if we're still turning alright
        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderGroundTrajectory2D(initializationState.groundTrajectoryInfo, 50, Color.CYAN);
        renderer.drawString3d("initial turn", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
    }
}
