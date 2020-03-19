package rlbotexample.bot_behaviour.car_destination;

import rlbotexample.input.dynamic_data.DataPacket;
import util.bezier_curve.CurveSegment;
import util.bezier_curve.PathComposite;
import util.bezier_curve.PathIterator;

class CarDestinationUpdater {

    private static final double DEFAULT_CAR_SPEED_VALUE = 500;
    private static final double BEZIER_ITERATOR_INTERPOLATION_PRECISION = 0.1;
    private static final double BOT_REFRESH_RATE = 30;

    private CarDestination desiredDestination;
    private PathComposite path;
    private PathIterator throttleIterator;
    private PathIterator steeringIterator;
    private double carSpeed;

    CarDestinationUpdater(CarDestination carDestination) {
        this.desiredDestination = carDestination;
        carSpeed = DEFAULT_CAR_SPEED_VALUE;
    }

    boolean hasNextThrottleDestination() {
        return throttleIterator.hasNext();
    }

    void nextDestination(DataPacket input) {
        desiredDestination.setThrottleDestination(throttleIterator.next());
        // calculate the direction in which the car is going to try to go
        steeringIterator.setT(throttleIterator.getT());
        steeringIterator.setLengthIncrement(getSteeringLengthIncrement(input));
        if(steeringIterator.hasNext()) {
            desiredDestination.setSteeringDestination(steeringIterator.next());
        }
        else {
            desiredDestination.setSteeringDestination(path.interpolate(1));
        }
    }

    void pathLengthIncreased(int numberOfAddedPaths, int numberOfPaths) {
        throttleIterator.pathLengthIncreased(numberOfAddedPaths, numberOfPaths);
    }

    private double getSteeringLengthIncrement(DataPacket input) {
        return CarDestination.getLocal(desiredDestination.getThrottleDestination(), input).magnitude()*0.2 +  input.car.velocity.magnitude()/4 + 200;
    }

    void setPath(PathComposite path) {
        this.path = path;
        throttleIterator = new PathIterator(path, carSpeed/BOT_REFRESH_RATE, BEZIER_ITERATOR_INTERPOLATION_PRECISION);
        steeringIterator = new PathIterator(path, 0, BEZIER_ITERATOR_INTERPOLATION_PRECISION);
        desiredDestination.setThrottleDestination(this.path.interpolate(0));
        desiredDestination.setSteeringDestination(this.path.interpolate(0));
    }

    CurveSegment getPath() {
        return path;
    }

    void setSpeed(double carSpeed) {
        this.carSpeed = carSpeed;
    }

    double getSpeed() {
        return this.carSpeed;
    }
}
