package rlbotexample.bot_behaviour.car_destination;

import rlbotexample.input.dynamic_data.DataPacket;
import util.bezier_curve.CurveSegment;
import util.bezier_curve.PathComposite;
import util.vector.Vector3;


public class CarDestination {

    private CarDestinationUpdater destinationUpdater;

    private Vector3 throttleDestination;
    private Vector3 previousThrottleDestination;

    private Vector3 steeringDestination;
    private Vector3 previousSteeringDestination;

    private Vector3 aerialDestination;
    private Vector3 previousAerialDestination;

    public CarDestination() {
        destinationUpdater = new CarDestinationUpdater(this);

        throttleDestination = new Vector3();
        previousThrottleDestination = new Vector3();
        steeringDestination = new Vector3();
        previousSteeringDestination = new Vector3();
        aerialDestination = new Vector3();
        previousAerialDestination = new Vector3();
    }

    public double getDesiredSpeed() {
        return destinationUpdater.getSpeed();
    }

    public void setDesiredSpeed(double speed) {
        destinationUpdater.setSpeed(speed);
    }

    public void advanceOneStep(DataPacket input) {
        if(hasNext()) {
            next(input);
        }
    }

    public boolean hasNext() {
        return destinationUpdater.hasNextThrottleDestination();
    }

    private void next(DataPacket input) {
        destinationUpdater.nextDestination(input);
    }

    public void pathLengthIncreased(int numberOfAddedPaths, int numberOfPaths) {
        destinationUpdater.pathLengthIncreased(numberOfAddedPaths, numberOfPaths);
    }

    public void setPath(PathComposite path) {
        destinationUpdater.setPath(path);
    }

    public CurveSegment getPath() {
        return destinationUpdater.getPath();
    }

    public Vector3 getThrottleDestination() {
        return throttleDestination;
    }

    void setThrottleDestination(Vector3 throttleDestination) {
        this.previousThrottleDestination = this.throttleDestination;
        this.throttleDestination = throttleDestination;
    }

    public Vector3 getPreviousThrottleDestination() {
        return previousThrottleDestination;
    }

    public Vector3 getSteeringDestination(DataPacket input) {
       return steeringDestination;
    }

    void setSteeringDestination(Vector3 steeringDestination) {
        this.previousSteeringDestination = this.steeringDestination;
        this.steeringDestination = steeringDestination;
    }

    public Vector3 getPreviousSteeringDestination() {
        return previousSteeringDestination;
    }

    public Vector3 getAerialDestination() {
        return aerialDestination;
    }

    public void setAerialDestination(Vector3 aerialDestination) {
        this.previousAerialDestination = this.aerialDestination;
        this.aerialDestination = aerialDestination;
    }

    public Vector3 getPreviousAerialDestination() {
        return previousAerialDestination;
    }

    public static Vector3 getLocal(Vector3 globalPosition, DataPacket input) {
        Vector3 myPosition = input.car.position;
        Vector3 myNoseVector = input.car.orientation.noseVector;
        Vector3 myRoofVector = input.car.orientation.roofVector;

        return globalPosition.minus(myPosition).toFrameOfReference(myNoseVector, myRoofVector);
    }

    public double getSteeringLengthIncrement(DataPacket input) {
        return destinationUpdater.getSteeringLengthIncrement(input);
    }
}
