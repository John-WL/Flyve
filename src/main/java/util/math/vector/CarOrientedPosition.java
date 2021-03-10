package util.math.vector;

import rlbotexample.input.dynamic_data.car.orientation.CarOrientation;
import rlbotexample.input.dynamic_data.car.orientation.Orientation;

import java.io.Serializable;

public class CarOrientedPosition implements Serializable {
    public Vector3 position;
    public CarOrientation orientation;

    public CarOrientedPosition(Vector3 position, CarOrientation orientation) {
        this.position = position;
        this.orientation = orientation;
    }

    public ZyxOrientedPosition toZyxOrientedPosition() {
        Vector2 flatRestOrientation = new Vector2(1, 0);
        Vector2 flatFront = orientation.noseVector.flatten();
        double angleZ = -flatRestOrientation.correctionAngle(flatFront);

        CarOrientation orientationRotatedInZ = orientation.rotate(Vector3.UP_VECTOR.scaled(angleZ));
        Vector3 rotatorY = orientationRotatedInZ.noseVector.findRotator(Vector3.X_VECTOR);
        double angleY = rotatorY.dotProduct(Vector3.Y_VECTOR);

        CarOrientation orientationRotatedInZy = orientationRotatedInZ.rotate(rotatorY);
        Vector3 rotatorX = orientationRotatedInZy.roofVector.findRotator(Vector3.UP_VECTOR);
        double angleX = rotatorX.dotProduct(Vector3.X_VECTOR);

        return new ZyxOrientedPosition(position, new Vector3(-angleX, -angleY, -angleZ));
    }
}
