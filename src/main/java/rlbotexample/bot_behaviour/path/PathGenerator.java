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

    private final static double DELAY_BEFORE_GETTING_NEXT_BALL_PREDICTION = 0;
    private static Timer ballPredictionTimer = new Timer(DELAY_BEFORE_GETTING_NEXT_BALL_PREDICTION);
    private static Vector3 lastBallPredictionPosition = new Vector3();

    public static void dummyPath(CarDestination desiredCarPosition) {
        Vector3 initialDirection = new Vector3(1, 0, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(0, 0, 0));
        controlPoints.add(new Vector3(1, 0, 0));

        initiateNewPath(controlPoints, initialDirection, desiredCarPosition);
    }

    public static void stupidPlayerChasePathGenerator(CarDestination desiredCarPosition, DataPacket input) {
        Vector3 opponentPosition = input.allCars.get(1-input.playerIndex).position;

        // adding the next position
        desiredCarPosition.getPath().addPoints(opponentPosition);
        // updating the t variable in the path composite
        desiredCarPosition.pathLengthIncreased(1, desiredCarPosition.getPath().getPoints().size());
    }

    public static void opponentChasePathGenerator(CarDestination desiredCarPosition, DataPacket input) {
        Vector3 opponentPosition = input.allCars.get(1-input.playerIndex).position;
        Vector3 myNoseVector = input.car.orientation.noseVector;

        // adding the next position
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(opponentPosition);
        controlPoints.add(opponentPosition.plus(new Vector3(0, 0, 1)));

        initiateNewPath(controlPoints, myNoseVector, desiredCarPosition);
    }

    public static void randomGroundPath(CarDestination desiredCarPosition, DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 myPosition = input.car.position;
            Vector3 myNoseVector = input.car.orientation.noseVector;

            List<Vector3> controlPoints = new ArrayList<>();
            controlPoints.add(myPosition);

            double x;
            double y;
            for(int i = 0; i < 10; i++) {
                x = ((Math.random()-0.5)*2)*3000;
                y = ((Math.random()-0.5)*2)*4000;
                controlPoints.add(new Vector3(x, y, 50));
            }

            initiateNewPath(controlPoints, myNoseVector, desiredCarPosition);
        }
    }

    public static void randomGroundPath2(CarDestination desiredCarPosition, DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 myPosition = input.car.position;
            Vector3 myNoseVector = input.car.orientation.noseVector;

            List<Vector3> controlPoints = new ArrayList<>();
            controlPoints.add(myPosition);

            double x;
            double y;
            for(int i = 0; i < 10; i++) {
                x = ((Math.random()-0.5)*2)*2500;
                y = ((Math.random()-0.5)*2)*3500;
                controlPoints.add(new Vector3(x, y, 50));
            }

            initiateNewPath(controlPoints, myNoseVector, desiredCarPosition);
        }
    }

    public static void randomAerialPath(CarDestination desiredCarPosition, DataPacket input) {
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

            initiateNewPath(controlPoints, myNoseVector, desiredCarPosition);
        }
    }

    public static void lethamyrAerialIcyMapThing(CarDestination desiredCarPosition, DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 myPosition = input.car.position;
            Vector3 myNoseVector = input.car.orientation.noseVector;
            List<Vector3> controlPoints = new ArrayList<>();
            controlPoints.add(myPosition);
            controlPoints.add(new Vector3(0, 0, 800));

            // level 1
            controlPoints.add(new Vector3(800, -1500, 800));
            controlPoints.add(new Vector3(2500, -5000, 1600));
            controlPoints.add(new Vector3(4100, -8000, 1500));
            controlPoints.add(new Vector3(8000, -13000, 1500));
            controlPoints.add(new Vector3(8500, -15500, 1500));

            // level 2
            controlPoints.add(new Vector3(8500, -20000, 4500));
            controlPoints.add(new Vector3(6800, -23000, 5000));
            controlPoints.add(new Vector3(6000, -25500, 6000));
            controlPoints.add(new Vector3(4000, -26700, 6400));
            controlPoints.add(new Vector3(1200, -27500, 5000));

            // level 3
            controlPoints.add(new Vector3(-1700, -24300, 4750));
            controlPoints.add(new Vector3(-4000, -22700, 3600));
            controlPoints.add(new Vector3(-8320, -22100, 2100));
            controlPoints.add(new Vector3(-11480, -20580, 2700));
            controlPoints.add(new Vector3(-16160, -19280, 3500));

            // level 4
            controlPoints.add(new Vector3(-19580, -18320, 3140));
            controlPoints.add(new Vector3(-22510, -15370, 2230));
            controlPoints.add(new Vector3(-22520, -14590, 2320));
            controlPoints.add(new Vector3(-22320, -13730, 2560));
            controlPoints.add(new Vector3(-21090, -12980, 3280));
            controlPoints.add(new Vector3(-19540, -13450, 3450));
            controlPoints.add(new Vector3(-18130, -12780, 3030));
            controlPoints.add(new Vector3(-15450, -9770, 3100));

            // level 5
            controlPoints.add(new Vector3(-11660, -5050, 3080));
            controlPoints.add(new Vector3(-11800, -2750, 2830));
            controlPoints.add(new Vector3(-12050, -2190, 2690));
            controlPoints.add(new Vector3(-9580, 1060, 1780));
            controlPoints.add(new Vector3(-8690, 3770, 710));
            controlPoints.add(new Vector3(-10270, 6000, 880));
            controlPoints.add(new Vector3(-13860, 10270, 2700));

            // level 6
            controlPoints.add(new Vector3(-17750, 15480, 2660));
            controlPoints.add(new Vector3(-19690, 16850, 3620));
            controlPoints.add(new Vector3(-20370, 17680, 4690));
            controlPoints.add(new Vector3(-19990, 18830, 5040));
            controlPoints.add(new Vector3(-18760, 19740, 5110));
            controlPoints.add(new Vector3(-17470, 20860, 4960));
            controlPoints.add(new Vector3(-16490, 21290, 5000));
            controlPoints.add(new Vector3(-15060, 21020, 5870));
            controlPoints.add(new Vector3(-14000, 20030, 5320));
            controlPoints.add(new Vector3(-12920, 19710, 5200));
            controlPoints.add(new Vector3(-11350, 19910, 5150));
            controlPoints.add(new Vector3(-10420, 19660, 4670));
            controlPoints.add(new Vector3(-8670, 17000, 2890));
            controlPoints.add(new Vector3(-7150, 16220, 2830));
            controlPoints.add(new Vector3(-5030, 16810, 3090));
            controlPoints.add(new Vector3(-2780, 18180, 4690));
            controlPoints.add(new Vector3(-1020, 19070, 5150));
            controlPoints.add(new Vector3(1870, 20580, 5800));

            // these take so many time to find... omg

            initiateNewPath(controlPoints, myNoseVector, desiredCarPosition);
        }
    }

    public static void simpleAerialPath1Generator(CarDestination desiredCarPosition, DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 initialDirection = new Vector3(0, -1, 0);
            List<Vector3> controlPoints = new ArrayList<>();
            controlPoints.add(new Vector3(1000, 0, 50));
            controlPoints.add(new Vector3(0, 1000, 50));
            controlPoints.add(new Vector3(-500, 500, 50));
            controlPoints.add(new Vector3(-1000, 0, 1000));
            controlPoints.add(new Vector3(-1000, 0, 10000));

            initiateNewPath(controlPoints, initialDirection, desiredCarPosition);
        }
    }

    public static void playerNetPositionPathGenerator(CarDestination desiredCarPosition, DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 initialDirection = new Vector3(0, -1, 0);
            List<Vector3> controlPoints = new ArrayList<>();
            if(input.car.team == 1) {
                controlPoints.add(new Vector3(0, 5500, 50));
                controlPoints.add(new Vector3(0, 5501, 50));
            }
            else {
                controlPoints.add(new Vector3(0, -5500, 50));
                controlPoints.add(new Vector3(0, -5501, 50));
            }

            initiateNewPath(controlPoints, initialDirection, desiredCarPosition);
        }
    }

    public static void ennemyNetPositionPathGenerator(CarDestination desiredCarPosition, DataPacket input) {
        if(!desiredCarPosition.hasNext()) {
            Vector3 initialDirection = new Vector3(0, -1, 0);
            List<Vector3> controlPoints = new ArrayList<>();
            if(input.car.team == 0) {
                double playerDistanceFromGoal = input.car.position.minus(new Vector3(0, 5500, 50)).magnitude();
                controlPoints.add(new Vector3(0, 5500 - Math.min(playerDistanceFromGoal/2, 2000), 50));
                controlPoints.add(new Vector3(0, 5501 - Math.min(playerDistanceFromGoal/2, 2000), 50));
            }
            else {
                double playerDistanceFromGoal = input.car.position.minus(new Vector3(0, -5500, 50)).magnitude();
                controlPoints.add(new Vector3(0, -5500 + Math.min(playerDistanceFromGoal/2, 2000), 50));
                controlPoints.add(new Vector3(0, -5501 + Math.min(playerDistanceFromGoal/2, 2000), 50));
            }

            initiateNewPath(controlPoints, initialDirection, desiredCarPosition);
        }
    }

    public static void ballChasePredictionPath(CarDestination desiredCarPosition, DataPacket input) {
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
        initiateNewPath(controlPoints, steeringDestination.minus(destination), desiredCarPosition);
    }

    public static void stupidBallChasePath(CarDestination desiredCarPosition, DataPacket input) {
        Vector3 destination = desiredCarPosition.getThrottleDestination();
        Vector3 steeringDestination = desiredCarPosition.getSteeringDestination(input);

        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(input.ball.position);
        controlPoints.add(input.ball.position.minus(new Vector3(0, 0, 1)));

        // generating the next path
        initiateNewPath(controlPoints, steeringDestination.minus(destination), desiredCarPosition);
    }

    private static Vector3 getFutureExpectedBallPosition(CarDestination desiredCarPosition, DataPacket input) {
        if(ballPredictionTimer.isTimeElapsed()) {
            ballPredictionTimer.start();
            lastBallPredictionPosition = getBallPredictionIfDelayIsOver(desiredCarPosition, input);
        }

        return lastBallPredictionPosition;
    }

    private static Vector3 getBallPredictionIfDelayIsOver(CarDestination desiredCarPosition, DataPacket input) {
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
                timeToGo = myPosition.minus(currentBallPosition).magnitude()/input.car.velocity.magnitude();

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

    private static void initiateNewPath(List<Vector3> controlPoints, Vector3 initialDirection, CarDestination desiredCarPosition) {
        desiredCarPosition.setPath(new QuadraticPath(controlPoints, initialDirection));
    }

}
