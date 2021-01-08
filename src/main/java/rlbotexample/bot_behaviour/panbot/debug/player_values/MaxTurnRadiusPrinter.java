package rlbotexample.bot_behaviour.panbot.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DrivingSpeedController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.GroundDrivingTrajectoryFinder;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.output.BotOutput;
import util.math.vector.Ray3;
import util.math.vector.Vector2;
import util.math.vector.Vector3;
import util.renderers.ShapeRenderer;
import util.shapes.Circle;

import java.awt.*;

public class MaxTurnRadiusPrinter extends FlyveBot {

    Trajectory3D rightTurn;
    Trajectory3D leftTurn;

    Vector3 ballDestination;
    Ray3 playerDestination;

    DrivingSpeedController drivingSpeedController = new DrivingSpeedController(this);

    public MaxTurnRadiusPrinter() {
        super();
        rightTurn = null;
        leftTurn = null;
        ballDestination = new Vector3(0, 5200, 100);
        playerDestination = new Ray3();
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        rightTurn = GroundDrivingTrajectoryFinder.getRightTurningTrajectory(input.allCars.get(1-input.playerIndex));
        leftTurn = GroundDrivingTrajectoryFinder.getLeftTurningTrajectory(input.allCars.get(1-input.playerIndex));

        //output().boost(true);
        drivingSpeedController.setSpeed(1400);
        drivingSpeedController.updateOutput(input);
        output().steer(1);

        return output();
    }

    private Circle getClosestFromPoint(Vector2 point, Circle c1, Circle c2) {
        if(c1.getCenter().minus(point).magnitude() < c2.getCenter().minus(point).magnitude()) {
            return c1;
        }
        return c2;
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        ShapeRenderer sr = new ShapeRenderer(renderer);

        sr.renderTrajectory(rightTurn, 3, Color.cyan);
        sr.renderTrajectory(leftTurn, 3, Color.cyan);

        //sr.renderCircle(rightTurnForBall, 9, Color.magenta);
        //sr.renderCircle(leftTurnForBall, 9, Color.magenta);
    }
}
