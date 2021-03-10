package rlbotexample.input.animations;

import rlbotexample.bot.implementation.physics.PhysicsOfBossBattle;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.ZyxOrientedPosition;

import java.util.concurrent.atomic.AtomicInteger;

public class CarGroupAnimator {
    private final CarGroupAnimation meshAnimation;
    private int frameCount;
    private final AtomicInteger safeBotIndex;

    public CarGroupAnimator(CarGroupAnimation meshAnimation) {
        this.meshAnimation = meshAnimation;
        this.frameCount = 0;
        this.safeBotIndex = new AtomicInteger(0);
    }

    public void step(DataPacket input) {
        safeBotIndex.set(0);
        input.allCars.forEach(carData -> {
            if(input.humanCar.playerIndex != carData.playerIndex) {
                try {
                    ZyxOrientedPosition orientedPosition = meshAnimation.get(frameCount)
                            .orientedPositions.get(safeBotIndex.get());
                    PhysicsOfBossBattle.setOrientedPosition(orientedPosition, carData);
                    safeBotIndex.incrementAndGet();
                }
                catch (Exception ignored) {}
            }
        });
        frameCount++;
    }

    public boolean isFinished() {
        return frameCount >= meshAnimation.frames.size();
    }

    public void reset() {
        frameCount = 0;
    }
}
