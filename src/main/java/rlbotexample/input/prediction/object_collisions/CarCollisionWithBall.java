package rlbotexample.input.prediction.object_collisions;

import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;

public class CarCollisionWithBall {

    final CarData initialCar;

    public CarCollisionWithBall(final CarData carData, final BallData ballData) {
        this.initialCar = carData;
    }

    public CarData compute(final double deltaTime) {
        return initialCar;
    }
}
