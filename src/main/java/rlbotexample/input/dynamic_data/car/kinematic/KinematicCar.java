package rlbotexample.input.dynamic_data.car.kinematic;

import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import util.math.vector.Vector3;

public class KinematicCar extends KinematicPoint {

    private HitBox hitBox;

    public KinematicCar(Vector3 position, Vector3 speed, Vector3 spin, HitBox hitBox, double gameTime) {
        super(position, speed, spin, gameTime);
        this.hitBox = hitBox;
    }

}
