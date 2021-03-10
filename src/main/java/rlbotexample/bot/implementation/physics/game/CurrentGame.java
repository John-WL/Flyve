package rlbotexample.bot.implementation.physics.game;

import rlbotexample.input.animations.CarGroupAnimator;
import rlbotexample.input.animations.GameAnimations;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.orientation.CarOrientation;
import util.math.vector.CarOrientedPosition;
import util.math.vector.Vector3;

public class CurrentGame {

    private static CarGroupAnimator boss_basic_rotation = new CarGroupAnimator(GameAnimations.boss_basic_rotation);

    public static void step(DataPacket input) {
        // testing if we can now animate the main bone of the animation in java:
        boss_basic_rotation.orientedPosition = new CarOrientedPosition(new Vector3(), input.humanCar.orientation);
        boss_basic_rotation.step(input);
    }
}
