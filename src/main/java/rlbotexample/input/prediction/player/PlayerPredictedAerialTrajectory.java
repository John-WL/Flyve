package rlbotexample.input.prediction.player;

import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.prediction.Parabola3D;
import util.game_constants.RlConstants;
import util.vector.Vector3;

public class PlayerPredictedAerialTrajectory {

    private final Parabola3D ballTrajectory;
    private final CarData initialCarData;

    public PlayerPredictedAerialTrajectory(CarData carData) {
        this.ballTrajectory = new Parabola3D(carData.position, carData.velocity, new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH), 0);
        this.initialCarData = carData;
    }

    public CarData compute(double deltaTime) {
        final Vector3 newPosition = ballTrajectory.compute(deltaTime);
        final Vector3 newVelocity = ballTrajectory.derivative(deltaTime);

        return new CarData(newPosition, newVelocity, initialCarData.spin, initialCarData.boost, initialCarData.hitBox, 0);
    }
}
