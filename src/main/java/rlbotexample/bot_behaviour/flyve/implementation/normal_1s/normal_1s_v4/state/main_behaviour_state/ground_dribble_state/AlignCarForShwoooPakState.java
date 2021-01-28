package rlbotexample.bot_behaviour.flyve.implementation.normal_1s.normal_1s_v4.state.main_behaviour_state.ground_dribble_state;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.GroundOrientationController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.GroundTrajectoryFinder;
import rlbotexample.input.prediction.Trajectory3D;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.state_machine.State;

import java.awt.*;

public class AlignCarForShwoooPakState implements State {

    private BotBehaviour bot;

    private DrivingSpeedController drivingSpeedController;
    private GroundOrientationController groundOrientationController;

    private Trajectory3D rightTurn;
    private Trajectory3D leftTurn;
    private Trajectory3D ballTrajectory;

    Vector3 destination;

    Vector3 closestPositionOfCar;
    Vector3 closestPositionOfBall;

    public AlignCarForShwoooPakState(BotBehaviour bot) {
        this.bot = bot;

        this.destination = new Vector3(0, -5200, 100);

        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
    }

    public void setBallDestination(Vector3 destination) {
        this.destination = destination;
    }

    @Override
    public void exec(DataPacket input) {
        drivingSpeedController.setSpeed(2300);
        drivingSpeedController.updateOutput(input);

        rightTurn = GroundTrajectoryFinder.getRightTurningTrajectory(input.car);
        leftTurn = GroundTrajectoryFinder.getLeftTurningTrajectory(input.car);
        ballTrajectory = input.statePrediction.ballAsTrajectory();

        double timeToCheck = 0.6;

        closestPositionOfCar = rightTurn.compute(timeToCheck);
        closestPositionOfCar = leftTurn.compute(timeToCheck);
        closestPositionOfBall = ballTrajectory.compute(timeToCheck);

        if(input.ball.velocity.orderedMinusAngle(destination.minus(input.ball.position)).y > 0) {
            closestPositionOfCar = rightTurn.compute(timeToCheck);
        }
        else {
            closestPositionOfCar = leftTurn.compute(timeToCheck);
        }
        Vector3 groundOrientation = input.car.orientation.noseVector
                .orderedPlusAngle(closestPositionOfBall.minus(input.car.position)
                        .orderedMinusAngle(closestPositionOfCar.minus(input.car.position)));
        Vector3 groundDestination = groundOrientation.scaled(300).plus(input.car.position);

        groundOrientationController.setDestination(groundDestination);
        groundOrientationController.updateOutput(input);

        bot.output().boost(input.car.velocity.magnitude() < input.ball.velocity.magnitude());
    }

    @Override
    public State next(DataPacket input) {
        if(closestPositionOfCar.minus(closestPositionOfBall).magnitude() > 300) {
            return this;
        }
        return new ShwoooPakState(bot);
    }

    @Override
    public void debug(DataPacket input, Renderer renderer) {
        renderer.drawString3d("align car for \"shwooo pak\"", Color.YELLOW, input.car.position, 2, 2);
        ShapeRenderer shapeRenderer = new ShapeRenderer(renderer);
        shapeRenderer.renderTrajectory(rightTurn, 4, Color.CYAN);
        shapeRenderer.renderTrajectory(leftTurn, 4, Color.CYAN);
        shapeRenderer.renderTrajectory(ballTrajectory, 4, Color.magenta);

        renderer.drawLine3d(Color.GREEN, closestPositionOfCar, closestPositionOfBall);
    }
}
