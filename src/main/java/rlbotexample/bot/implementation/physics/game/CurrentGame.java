package rlbotexample.bot.implementation.physics.game;

import rlbotexample.bot.implementation.physics.PhysicsOfBossBattle;
import rlbotexample.input.animations.CarGroupAnimator;
import rlbotexample.input.animations.GameAnimations;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.ZyxOrientedPosition;
import util.math.vector.Vector3;

public class CurrentGame {

    private static CarGroupAnimator boss_basic_rotation = new CarGroupAnimator(GameAnimations.boss_attack);

    public static void step(DataPacket input) {
        if(input.humanCar.hasWheelContact) {
            boss_basic_rotation.step(input);
            if(boss_basic_rotation.isFinished()) {
                boss_basic_rotation.reset();
            }
        }
    }
}
