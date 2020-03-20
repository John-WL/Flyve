package rlbotexample.bot_behaviour.path;

import rlbot.cppinterop.RLBotDll;
import rlbot.flat.BallPrediction;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.input.dynamic_data.DataPacket;
import util.bezier_curve.QuadraticPath;
import util.timer.Timer;
import util.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class PathGenerator {

    private CarDestination desiredCarPosition;

    public PathGenerator(CarDestination desiredCarPosition) {
        this.desiredCarPosition = desiredCarPosition;
    }

    private final static double DELAY_BEFORE_GETTING_NEXT_BALL_PREDICTION = 0;
    private static Timer ballPredictionTimer = new Timer(DELAY_BEFORE_GETTING_NEXT_BALL_PREDICTION);
    private static Vector3 lastBallPredictionPosition = new Vector3();

    public void dummyPath() {
        Vector3 initialDirection = new Vector3(1, 0, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(0, 0, 0));
        controlPoints.add(new Vector3(1, 0, 0));

        initiateNewPath(controlPoints, initialDirection);
    }

    public void stupidPlayerChasePathGenerator(DataPacket input) {
        Vector3 opponentPosition = input.allCars.get(1-input.playerIndex).position;

        // adding the next position
        desiredCarPosition.getPath().addPoints(opponentPosition);
        // updating the t variable in the path composite
        desiredCarPosition.pathLengthIncreased(1, desiredCarPosition.getPath().getPoints().size());
    }

    public void randomGroundPath(DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 myPosition = input.car.position;
            Vector3 myNoseVector = input.car.orientation.noseVector;

            List<Vector3> controlPoints = new ArrayList<>();
            controlPoints.add(myPosition);

            double x = 0;
            double y = 0;
            for(int i = 0; i < 10; i++) {
                x = ((Math.random()-0.5)*2)*3000;
                y = ((Math.random()-0.5)*2)*4000;
                controlPoints.add(new Vector3(x, y, 50));
            }

            initiateNewPath(controlPoints, myNoseVector);
        }
    }

    public void randomAerialPath(DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 myPosition = input.car.position;
            Vector3 myNoseVector = input.car.orientation.noseVector;
            List<Vector3> controlPoints = new ArrayList<>();
            controlPoints.add(myPosition);

            double x;
            double y;
            double z;
            for (int i = 0; i < 10; i++) {
                x = ((Math.random() - 0.5) * 2) * 3000;
                y = ((Math.random() - 0.5) * 2) * 4000;
                z = Math.random() * 600;
                controlPoints.add(new Vector3(x, y, 800 + z));
            }

            initiateNewPath(controlPoints, myNoseVector);
        }
    }

    public void simpleAerialPath1Generator(DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 initialDirection = new Vector3(0, -1, 0);
            List<Vector3> controlPoints = new ArrayList<>();
            controlPoints.add(new Vector3(1000, 0, 50));
            controlPoints.add(new Vector3(0, 1000, 50));
            controlPoints.add(new Vector3(-500, 500, 50));
            controlPoints.add(new Vector3(-1000, 0, 1000));
            controlPoints.add(new Vector3(-1000, 0, 10000));

            initiateNewPath(controlPoints, initialDirection);
        }
    }

    public void ballChasePredictionPath(DataPacket input) {
        // get the future expected ball position
        Vector3 futureBallPosition = getFutureExpectedBallPosition(desiredCarPosition, input);
        Vector3 destination = desiredCarPosition.getThrottleDestination();
        Vector3 steeringDestination = desiredCarPosition.getSteeringDestination(input);

        // creating the next path. Here, we do a little trick so we can generate
        // a new end point that goes to the ball prediction every frame.
        // It basically cuts the current path to where the throttle position is,
        // and creates a new path that starts there and ends where the
        // new predicted ball is.
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(destination);
        controlPoints.add(futureBallPosition.minus(new Vector3(0, 0, 50)));

        // generating the next path
        initiateNewPath(controlPoints, steeringDestination.minus(destination));
    }

    private Vector3 getFutureExpectedBallPosition(CarDestination desiredCarPosition, DataPacket input) {
        if(ballPredictionTimer.isTimeElapsed()) {
            ballPredictionTimer.start();
            lastBallPredictionPosition = getBallPredictionIfDelayIsOver(input);
        }

        return lastBallPredictionPosition;
    }

    private Vector3 getBallPredictionIfDelayIsOver(DataPacket input) {
        try {
            // Get the "thanks-god" implementation of the ball prediction and use it to find
            // the next likely future position
            Vector3 myPosition = input.car.position;
            Vector3 currentBallPosition = input.ball.position;
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            Vector3 futureBallPosition = new Vector3(ballPrediction.slices(0).physics().location());
            double divisor = (double)ballPrediction.slicesLength()/2;
            int currentBallPositionIndex = (int)divisor;
            double initialBallTime;
            double futureBallTime;
            double timeToGo;

            // pinpoint the position where PanBot will hit the ball
            while(divisor >= 1) {
                divisor /= 2;
                futureBallPosition = new Vector3(ballPrediction.slices(currentBallPositionIndex).physics().location());
                initialBallTime = ballPrediction.slices(0).gameSeconds();
                futureBallTime = ballPrediction.slices(currentBallPositionIndex).gameSeconds();
                timeToGo = myPosition.minus(currentBallPosition).magnitude()/desiredCarPosition.getDesiredSpeed();

                if(timeToGo > futureBallTime - initialBallTime) {
                    currentBallPositionIndex += divisor;
                }
                else {
                    currentBallPositionIndex -= divisor;
                }
            }

            // return the avergae between the predicted ball position and the current one
            return futureBallPosition.plus(currentBallPosition).scaled(0.5);
        }
        catch(Exception e) {
            e.printStackTrace();

            // return a 0ed vector if ball prediction is not working.
            return new Vector3();
        }
    }

    private void initiateNewPath(List<Vector3> controlPoints, Vector3 initialDirection) {
        desiredCarPosition.setPath(new QuadraticPath(controlPoints, initialDirection));
    }

}
