package rlbotexample.bot_behaviour.path;

import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.input.dynamic_data.DataPacket;
import util.bezier_curve.QuadraticPath;
import util.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class PathGenerator {

    public static void dummyPath(CarDestination desiredCarPosition) {
        Vector3 initialDirection = new Vector3(1, 0, 0);
        List<Vector3> controlPoints = new ArrayList<>();
        controlPoints.add(new Vector3(0, 0, 0));
        controlPoints.add(new Vector3(1, 0, 0));

        initiateNewPath(controlPoints, initialDirection, desiredCarPosition);
    }

    public static void randomGroundPath(CarDestination desiredCarPosition, DataPacket input) {
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

            initiateNewPath(controlPoints, myNoseVector, desiredCarPosition);
        }
    }

    private static void initiateNewPath(List<Vector3> controlPoints, Vector3 initialDirection, CarDestination desiredCarPosition) {
        desiredCarPosition.setPath(new QuadraticPath(controlPoints, initialDirection));
    }

}
