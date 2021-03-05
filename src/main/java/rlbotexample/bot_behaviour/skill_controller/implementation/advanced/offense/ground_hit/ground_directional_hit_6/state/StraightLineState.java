package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit.ground_directional_hit_6.state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectory2DInfo;
import util.math.vector.Ray2;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.state_machine.State;

import java.awt.*;

public class StraightLineState implements State {

    private final BotBehaviour bot;
    private final InitializationState initializationState;

    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController2 groundOrientationController;

    public StraightLineState(BotBehaviour bot, InitializationState initializationState) {
        this.bot = bot;
        this.initializationState = initializationState;

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController2(bot);
    }

    @Override
    public void exec(DataPacket input) {
        drivingSpeedController.setSpeed(1200);
        drivingSpeedController.updateOutput(input);

        Ray2 straightLine = initializationState.groundTrajectoryInfo.straightLine;
        Vector2 steeringDestination = straightLine.offset.plus(straightLine.direction);
        groundOrientationController.setDestination(new Vector3(steeringDestination, 50));
        groundOrientationController.updateOutput(input);
        //bot.output().steer(0);
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

        // if we reached the point where we need to do the final turn, just change the state to that one
        if(groundTrajectoryInfo.hasPassedStraightLine(timeElapsedFromTrajectoryGeneration, input.car.velocity.magnitude())) {
            return new FinalTurnState(bot, initializationState);
        }
        // if we "got lost" while driving (aka are we too far away from the expected point?), then re-compute the ground trajectory
        // with the initialization state
        if(expectedPositionOnTrajectory.minus(input.car.position.flatten()).magnitude() > 10) {
            return initializationState;
        }

        // stay in this state if we're still doing alright
        return this;
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderGroundTrajectory2D(initializationState.groundTrajectoryInfo, 50, Color.CYAN);
        renderer.drawString3d("straight line", Color.YELLOW, input.car.position.toFlatVector(), 2, 2);
    }
}
