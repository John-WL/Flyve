package rlbotexample.bot_behaviour;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.bot_movements.MovementOutputHandler;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.path.PathGenerator;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.debug.BezierDebugger;
import util.vector.Vector3;
import java.awt.Color;

// Pan is an abbreviation for PATCHES ARE NEEDED!
public class PanBot extends BotBehaviour {

    private CarDestination desiredDestination;
    private MovementOutputHandler movementOutputHandler;

    public PanBot() {
        desiredDestination = new CarDestination();
        movementOutputHandler = new MovementOutputHandler(desiredDestination, this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        // make sure the path is always up to date with the data packet
        PathGenerator.ballChasePredictionPath(desiredDestination, input);

        // bot's desired position advances one step
        desiredDestination.step(input);

        // calculate what output the bot needs to have to reach the just advanced step
        movementOutputHandler.actualizeBotOutput(input);

        // return the calculated bot output
        return super.output();
    }

    /*
    private void stupidBallChasePathGenerator(DataPacket input) {
        Vector3 ballPosition = input.ball.position;

        // adding the next position
        myPath.addPoints(ballPosition);
        // updating the t variable in the path composite
        double t = myPathIterator.getT();
        double numberOfPaths = myPath.getNumberOfComponents();
        myPathIterator.setT((t * numberOfPaths) / (numberOfPaths + 1));
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
        Vector3 initialDirection = new Vector3(1, 0, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(0, 0, 0));
        controlPoints.add(new Vector3(1, 0, 0));

        initiateNewPath(controlPoints, initialDirection);
    }

    private void generateRandomGroundPath(DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 myNoseVector = input.car.orientation.noseVector;

        Vector3 initialDirection = myNoseVector;
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(myPosition);

        double x = 0;
        double y = 0;
        for(int i = 0; i < 10; i++) {
            x = ((Math.random()-0.5)*2)*3000;
            y = ((Math.random()-0.5)*2)*4000;
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
            z = Math.random()*1500;
            controlPoints.add(new Vector3(x, y, 500 + z));
        }

        initiateNewPath(controlPoints, initialDirection);
    }

    private void generateBallChasePredictionPath(DataPacket input) {
        // get the future expected ball position
        Vector3 futureBallPosition = getFutureExpectedBallPosition(input);

        // creating the next path. Here, we do a little trick so we can generate
        // a new end point that goes to the ball prediction every frame.
        // It basically cuts the current path to where the throttle position is,
        // and creates a new path that starts there and ends where the
        // new predicted ball is.
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(myDestination);
        controlPoints.add(futureBallPosition.minus(new Vector3(0, 0, 50)));

        // generating the next path
        initiateNewPath(controlPoints, mySteeringDestination.minus(myDestination));
    }

    private Vector3 getFutureExpectedBallPosition(DataPacket input) {
        try {
            // Get the "thanks-god" implementation of the ball prediction and use it to find
            // the next likely future position
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            Vector3 myPosition = input.car.position;
            int divisor = ballPrediction.slicesLength()/2;
            int currentBallPositionIndex = divisor;
            Vector3 futureBallPosition = new Vector3(ballPrediction.slices(0).physics().location());
            double initialBallTime;
            double futureBallTime;
            double timeToGo;

            // pinpoint the position where PanBot will hit the ball
            while(divisor > 0) {
                divisor /= 2;
                futureBallPosition = new Vector3(ballPrediction.slices(currentBallPositionIndex).physics().location());
                initialBallTime = ballPrediction.slices(0).gameSeconds();
                futureBallTime = ballPrediction.slices(currentBallPositionIndex).gameSeconds();
                timeToGo = myPosition.minus(futureBallPosition).magnitude()/BEZIER_ITERATOR_SPEED;

                if(timeToGo > futureBallTime - initialBallTime) {
                    currentBallPositionIndex += divisor;
                }
                else {
                    currentBallPositionIndex -= divisor;
                }
            }

            return futureBallPosition;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // return a 0ed vector if ball prediction is not working.
        return new Vector3();
    }

    private void driveToDestination(DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 mySpeed = input.car.velocity;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;

        double throttleAmount = throttlePid.process(myLocalDestination.x, 0);
        if(myLocalSteeringDestination.x < 0) throttleAmount = -throttleAmount;

        double steerAmount = -steerPid.process(myLocalSteeringDestination.minusAngle(myPreviousLocalSteeringDestination).flatten().correctionAngle(new Vector2(1, 0)), myLocalSteeringDestination.flatten().correctionAngle(new Vector2(1, 0)));
        myPreviousLocalDestination = myLocalDestination;
        myPreviousLocalSteeringDestination = myLocalSteeringDestination;

        output().throttle(throttleAmount);
        output().boost(throttleAmount > 20000);

        output().steer(steerAmount);
        output().drift(Math.abs(steerAmount) > 5);

        if(myLocalDestination.z > 200) {
            isAerialing = true;
        }
        else if(myLocalDestination.z <= 200 && input.car.hasWheelContact){
            isAerialing = false;
        }

        if(isAerialing) {
            if(input.car.hasWheelContact) {
                if(output().jump()) {

                }
                output().jump(!output().jump());
            }*/

            /*double pitchAmount = pitchPid.process(myLocalAerialDestination.z, 0);
            double yawAmount = yawPid.process(-myLocalAerialDestination.y, 0);
            double rollAmount = rollPid.process(myLocalAerialDestination.x, 0);
            boolean aerialBoostState = aerialBoostPid.process(myPosition.minus(myDestination).z, 0) < 0;*/
            /*
            double pitchAmount = pitchPid.process(myPreviousLocalAerialDestination.z - myLocalAerialDestination.z, -myLocalAerialDestination.z);
            double yawAmount = yawPid.process(myPreviousLocalAerialDestination.y - myLocalAerialDestination.y, myLocalAerialDestination.y);
            double rollAmount = rollPid.process(myLocalAerialDestination.x, 0);
            boolean aerialBoostState = aerialBoostPid.process(myPosition.minus(myDestination).z, 0) < 0;
            myPreviousLocalAerialDestination = myLocalAerialDestination;

            output().pitch(pitchAmount);
            output().yaw(yawAmount);
            //output().roll(rollAmount);

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
    }*/

    public void displayDebugLines(Renderer renderer, DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 throttleDestination = desiredDestination.getThrottleDestination();
        Vector3 steeringDestination = desiredDestination.getSteeringDestination();
        Vector3 aerialDestination = desiredDestination.getAerialDestination();
        boolean isAerialing = movementOutputHandler.isAerialing();

        renderer.drawLine3d(Color.LIGHT_GRAY, myPosition, throttleDestination);
        renderer.drawLine3d(Color.MAGENTA, myPosition, steeringDestination);
        if(isAerialing) {
            renderer.drawLine3d(Color.green, myPosition, aerialDestination.minus(myPosition).scaledToMagnitude(300).plus(myPosition));
        }
        else {
            renderer.drawLine3d(Color.ORANGE, myPosition, aerialDestination.minus(myPosition).scaledToMagnitude(300).plus(myPosition));
        }
        BezierDebugger.renderPath(desiredDestination.getPath(), Color.blue, renderer);
    }
}
