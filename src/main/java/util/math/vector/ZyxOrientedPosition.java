package util.math.vector;

import rlbotexample.input.dynamic_data.car.orientation.CarOrientation;
import rlbotexample.input.dynamic_data.car.orientation.Orientation;

import java.io.Serializable;

public class ZyxOrientedPosition implements Serializable {
    public Vector3 position;
    public Vector3 eulerZYX;

    public ZyxOrientedPosition(Vector3 position, Vector3 eulerZYX) {
        this.position = position;
        this.eulerZYX = eulerZYX;
    }

    public CarOrientedPosition toCarOrientedPosition() {
        CarOrientation orientation = new CarOrientation(Vector3.X_VECTOR, Vector3.UP_VECTOR);
        Vector3 xAxisRotator = Vector3.X_VECTOR.scaled(eulerZYX.x);
        Vector3 yAxisRotator = Vector3.Y_VECTOR.scaled(eulerZYX.y);
        Vector3 zAxisRotator = Vector3.UP_VECTOR.scaled(eulerZYX.z);

        orientation = orientation.rotate(zAxisRotator);
        xAxisRotator = xAxisRotator.rotate(zAxisRotator);
        yAxisRotator = yAxisRotator.rotate(zAxisRotator);

        orientation = orientation.rotate(yAxisRotator);
        xAxisRotator = xAxisRotator.rotate(yAxisRotator);

        return new CarOrientedPosition(position, orientation.rotate(xAxisRotator));
    }
}
