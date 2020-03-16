package rlbotexample.bot_behaviour;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.bezier_curve.*;
import util.pid_controller.PidController;
import util.timer.Timer;
import util.debug.BezierDebugger;
import util.vector.Vector2;
import util.vector.Vector3;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

// Pan is an abbreviation for PATCHES ARE NEEDED!

public class PanBot extends BotBehaviour {

    private static final double REFRESH_RATE_OF_DESTINATION_ACTUALIZATION = 0.05;
    private static final double BEZIER_ITERATOR_SPEED = 400;
    private static final double BEZIER_ITERATOR_LENGTH_INCREMENT = BEZIER_ITERATOR_SPEED * REFRESH_RATE_OF_DESTINATION_ACTUALIZATION;
    private static final double BEZIER_ITERATOR_INTERPOLATION_PRECISION = 1;

    private Vector3 myDestination;
    private Vector3 mySteeringDestination;
    private Vector3 myLocalDestination;
    private Vector3 myLocalSteeringDestination;
    private PathComposite myPath;
    private PathIterator myPathIterator;
    private PathIterator steeringIterator;
    private Timer destinationRefreshRate;
    private PidController throttlePid;
    private PidController steerPid;
    private boolean isFlipping;

    public PanBot() {
        generateDummyPath();
        destinationRefreshRate = new Timer(REFRESH_RATE_OF_DESTINATION_ACTUALIZATION);

        // pid settings for a 0.05 seconds of refresh rate
        throttlePid = new PidController(0.04, 0.00001, 1);
        steerPid = new PidController(1, 0.00001, 0.3);


        isFlipping = false;
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // control the refresh rate of the bot (max 30 fps or so by the core implementation of RlBot)
        if(destinationRefreshRate.isTimeElapsed()) {
            destinationRefreshRate.start();
            updateDestination(input);
            driveToDestination(input);
        }

        return output();
    }

    private void updateDestination(DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;
        Vector3 ballPosition = input.ball.position;

        // generate the next path to take
        extremePlayerChasePathGenerator(input);
        // calculate the next intended destination on the curve
        if(myPathIterator.hasNext()) {
            myDestination = myPathIterator.next();
        }
        // update the local destination
        myLocalDestination = myDestination.minus(myPosition).toFrameOfReference(myNoseVector, myRoofVector);

        // calculate the direction in which the car is going to try to go
        steeringIterator.setT(myPathIterator.getT());
        steeringIterator.setLengthIncrement(getSteeringLengthIncrement(input));
        if(steeringIterator.hasNext()) {
            mySteeringDestination = steeringIterator.next();
        }
        else {
            mySteeringDestination = myPath.interpolate(1);
        }
        // update the local steering
        myLocalSteeringDestination = mySteeringDestination.minus(myPosition).toFrameOfReference(myNoseVector, myRoofVector);
    }

    private void extremeBallChasePathGenerator(DataPacket input) {
        Vector3 ballPosition = input.ball.position;

        // adding the next position
        myPath.addPoints(ballPosition);
        // updating the t variable in the path composite
        double t = myPathIterator.getT();
        double numberOfPaths = myPath.getNumberOfComponents();
        myPathIterator.setT((t * numberOfPaths) / (numberOfPaths + 1));
    }

    private void extremePlayerChasePathGenerator(DataPacket input) {
        Vector3 opponentPosition = input.allCars.get(1-input.playerIndex).position;

        // adding the next position
        myPath.addPoints(opponentPosition);
        // updating the t variable in the path composite
        double t = myPathIterator.getT();
        double numberOfPaths = myPath.getNumberOfComponents();
        myPathIterator.setT((t * numberOfPaths) / (numberOfPaths + 1));
    }

    private void generateDummyPath() {
        Vector3 initialDirection = new Vector3(0, -1, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(0, 0, 1));
        controlPoints.add(new Vector3(1000, 1000, 1));

        myPath = new QuadraticPath(controlPoints, initialDirection);
        myPathIterator = new PathIterator(myPath, BEZIER_ITERATOR_LENGTH_INCREMENT, BEZIER_ITERATOR_INTERPOLATION_PRECISION);
        steeringIterator = new PathIterator(myPath, 0, BEZIER_ITERATOR_INTERPOLATION_PRECISION);
        myDestination = myPath.interpolate(0);
        mySteeringDestination = myPath.interpolate(0);
    }

    private void generateRandomPath(DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 myNoseVector = input.car.orientation.noseVector;

        Vector3 initialDirection = myNoseVector;
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(myPosition);

        double x = 0;
        double y = 0;
        double z = 0;
        for(int i = 0; i < 10; i++) {
            x = ((Math.random()-0.5)*2)*3000;
            y = ((Math.random()-0.5)*2)*4000;
            z = Math.random()*1000;
            controlPoints.add(new Vector3(x, y, 100));
        }

        myPath = new QuadraticPath(controlPoints, initialDirection);
        myPathIterator = new PathIterator(myPath, BEZIER_ITERATOR_LENGTH_INCREMENT, BEZIER_ITERATOR_INTERPOLATION_PRECISION);
        if(myPathIterator.hasNext()) {
            myDestination = myPathIterator.next();
        }
        else {
            myDestination = myPath.interpolate(1);
        }
    }

    private void driveToDestination(DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 mySpeed = input.car.velocity;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;

        double throttleAmount = throttlePid.process(myLocalDestination.x / 100, 0);
        double steerAmount = steerPid.process(myLocalSteeringDestination.flatten().correctionAngle(new Vector2(1, 0)), 0);

        output().throttle(throttleAmount);
        //output().jump(isFlipping);
        output().boost(throttleAmount > 1);

        output().steer(steerAmount);
        output().drift(Math.abs(steerAmount) > 1);

        /*if(!input.car.hasWheelContact) {
            if(isFlipping) {
                output().pitch(-myLocalDestination.normalized().x);
                output().yaw(-myLocalDestination.normalized().y);
                output().jump(true);
                isFlipping = false;
            }
            else {
                output().pitch(0);
                output().yaw(0);
            }
        }
        isFlipping = (isFlipping || Math.abs(throttleAmount) > 2) && mySpeed.dotProduct(mySteeringDestination.minus(myPosition))/mySteeringDestination.minus(myPosition).magnitude() > 700;
        */
    }

    private double getSteeringLengthIncrement(DataPacket input) {
        return myLocalDestination.magnitude()*1.1 + input.car.velocity.magnitude()/4 + 200;
    }

    public void displayDebugLines(Renderer renderer, DataPacket input) {
        Vector3 myPosition = input.car.position;

        renderer.drawLine3d(Color.LIGHT_GRAY, myPosition, myDestination);
        renderer.drawLine3d(Color.MAGENTA, myPosition, mySteeringDestination);
        renderMyPath(renderer, Color.blue);
    }

    private void renderMyPath(Renderer renderer, Color color) {
        BezierDebugger.renderPath(renderer, color, myPath);
    }
}
