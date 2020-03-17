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

    private static final double REFRESH_RATE_OF_DESTINATION_ACTUALIZATION = 0.0333;
    private static final double BEZIER_ITERATOR_SPEED = 1000;
    private static final double BEZIER_ITERATOR_LENGTH_INCREMENT = BEZIER_ITERATOR_SPEED * REFRESH_RATE_OF_DESTINATION_ACTUALIZATION;
    private static final double BEZIER_ITERATOR_INTERPOLATION_PRECISION = 1;

    private Vector3 myDestination;
    private Vector3 mySteeringDestination;
    private Vector3 myAerialDestination;
    private Vector3 myLocalDestination;
    private Vector3 myPreviousLocalDestination;
    private Vector3 myLocalSteeringDestination;
    private Vector3 myPreviousLocalSteeringDestination;
    private Vector3 myLocalAerialDestination;
    private Vector3 myPreviousLocalAerialDestination;
    private PathComposite myPath;
    private PathIterator myPathIterator;
    private PathIterator steeringIterator;
    private Timer destinationRefreshRate;
    private PidController throttlePid;
    private PidController steerPid;
    private PidController pitchPid;
    private PidController yawPid;
    private PidController aerialOrientationXPid;
    private PidController aerialOrientationYPid;
    private PidController aerialBoostPid;
    private boolean isAerialing;
    private boolean isFlipping;

    public PanBot() {
        generateDummyPath();
        destinationRefreshRate = new Timer(REFRESH_RATE_OF_DESTINATION_ACTUALIZATION);

        // pid settings for a 0.05 seconds of refresh rate
        throttlePid = new PidController(0.001, 0, 0.01);
        steerPid = new PidController(1, 0, 1.2);
        pitchPid = new PidController(0.01, 0, 0.1);
        yawPid = new PidController(0.01, 0, 0.1);

        aerialOrientationXPid = new PidController(2, 0, 10);
        aerialOrientationYPid = new PidController(2, 0, 10);
        aerialBoostPid = new PidController(2, 0, 20);

        myPreviousLocalDestination = new Vector3();
        myPreviousLocalSteeringDestination = new Vector3();
        myPreviousLocalAerialDestination = new Vector3();

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
        Vector3 mySpeed = input.car.velocity;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;
        Vector3 ballPosition = input.ball.position;

        // generate the next path to take
        if(!myPathIterator.hasNext()) {
            generateRandomAerialPath(input);
        }
        // calculate the next intended destination on the curve
        if(myPathIterator.hasNext()) {
            myDestination = myPathIterator.next();
        }
        // update the local destination
        myLocalDestination = getLocal(myDestination, input);

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
        myLocalSteeringDestination = getLocal(mySteeringDestination, input);

        // update the direction when aerialing and the local direction when aerialing
        double myAerialDestinationX = -aerialOrientationXPid.process(mySpeed.x, myDestination.minus(myPosition).x);
        double myAerialDestinationY = -aerialOrientationYPid.process(mySpeed.y, myDestination.minus(myPosition).y);
        myAerialDestination = myDestination.plus(new Vector3(myAerialDestinationX, myAerialDestinationY, myDestination.minus(myPosition).magnitude()*3 + 300));
        myLocalAerialDestination = getLocal(myAerialDestination, input);
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

    private void simpleAerialPath1Generator(DataPacket input) {
        Vector3 initialDirection = new Vector3(0, -1, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(1000, 0, 50));
        controlPoints.add(new Vector3(0, 1000, 50));
        controlPoints.add(new Vector3(-500, 500, 50));
        controlPoints.add(new Vector3(-1000, 0, 1000));
        controlPoints.add(new Vector3(-1000, 0, 10000));

        initiateNewPath(controlPoints, initialDirection);
    }

    private void simpleAerialPath2Generator(DataPacket input) {
        Vector3 initialDirection = new Vector3(0, -1, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(1000, 0, 50));
        controlPoints.add(new Vector3(0, 1000, 50));
        controlPoints.add(new Vector3(-500, 500, 50));
        controlPoints.add(new Vector3(-1000, 0, 1000));
        controlPoints.add(new Vector3(1000, 0, 1000));
        controlPoints.add(new Vector3(0, 1000, 1000));
        controlPoints.add(new Vector3(0, -1000, 1000));
        controlPoints.add(new Vector3(-1000, 0, 1000));
        controlPoints.add(new Vector3(300, 400, 1000));
        controlPoints.add(new Vector3(-400, 800, 1000));
        controlPoints.add(new Vector3(1600, 300, 1000));

        initiateNewPath(controlPoints, initialDirection);
    }

    private void generateDummyPath() {
        Vector3 initialDirection = new Vector3(0, -1, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(0, 0, 1));
        controlPoints.add(new Vector3(1000, 1000, 1));

        initiateNewPath(controlPoints, initialDirection);
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
            controlPoints.add(new Vector3(x, y, 50));
        }

        initiateNewPath(controlPoints, initialDirection);
    }

    private void generateRandomAerialPath(DataPacket input) {
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
            controlPoints.add(new Vector3(x, y, 300 + z));
        }

        initiateNewPath(controlPoints, initialDirection);
    }

    private void driveToDestination(DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 mySpeed = input.car.velocity;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;

        double throttleAmount = throttlePid.process(myLocalDestination.x, 0);
        //double throttleAmount = throttlePid.process(myLocalDestination.x, 0);
        double steerAmount = -steerPid.process(myLocalSteeringDestination.minusAngle(myPreviousLocalSteeringDestination).flatten().correctionAngle(new Vector2(1, 0)), myLocalSteeringDestination.flatten().correctionAngle(new Vector2(1, 0)));
        myPreviousLocalDestination = myLocalDestination;
        myPreviousLocalSteeringDestination = myLocalSteeringDestination;

        output().throttle(throttleAmount);
        output().boost(throttleAmount > 1.5);

        output().steer(steerAmount);
        output().drift(Math.abs(steerAmount) > 1);

        if(myLocalDestination.z > 100) {
            isAerialing = true;
        }
        else if(myLocalDestination.z <= 100 && input.car.hasWheelContact){
            isAerialing = false;
        }

        if(isAerialing) {
            if(input.car.hasWheelContact) {
                output().jump(!output().jump());
                pitchPid.resetIntegralValue();
                yawPid.resetIntegralValue();
                aerialBoostPid.resetIntegralValue();
            }

            double pitchAmount = pitchPid.process(myLocalAerialDestination.z, 0);
            double yawAmount = yawPid.process(-myLocalAerialDestination.y, 0);
            boolean aerialBoostState = aerialBoostPid.process(myPosition.minus(myDestination).z, 0) < 0;

            output().pitch(pitchAmount);
            output().yaw(yawAmount);

            output().boost(aerialBoostState);
        }
    }

    private double getSteeringLengthIncrement(DataPacket input) {
        return myLocalDestination.magnitude()*0.2 +  input.car.velocity.magnitude()/4 + 200;
    }

    private Vector3 getLocal(Vector3 globalPoint, DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;

        return globalPoint.minus(myPosition).toFrameOfReference(myNoseVector, myRoofVector);
    }

    private void initiateNewPath(List<Vector3> controlPoints, Vector3 initialDirection) {
        myPath = new QuadraticPath(controlPoints, initialDirection);
        myPathIterator = new PathIterator(myPath, BEZIER_ITERATOR_LENGTH_INCREMENT, BEZIER_ITERATOR_INTERPOLATION_PRECISION);
        steeringIterator = new PathIterator(myPath, 0, BEZIER_ITERATOR_INTERPOLATION_PRECISION);
        myDestination = myPath.interpolate(0);
        mySteeringDestination = myPath.interpolate(0);
    }

    public void displayDebugLines(Renderer renderer, DataPacket input) {
        Vector3 myPosition = input.car.position;

        renderer.drawLine3d(Color.LIGHT_GRAY, myPosition, myDestination);
        renderer.drawLine3d(Color.MAGENTA, myPosition, mySteeringDestination);
        renderer.drawLine3d(Color.green, myPosition, myAerialDestination);
        renderMyPath(renderer, Color.blue);
    }

    private void renderMyPath(Renderer renderer, Color color) {
        BezierDebugger.renderPath(renderer, color, myPath);
    }
}
