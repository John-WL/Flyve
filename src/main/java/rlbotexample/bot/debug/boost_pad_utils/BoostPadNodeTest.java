package rlbotexample.bot.debug.boost_pad_utils;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.boost.BoostManager;
import rlbotexample.input.dynamic_data.boost.BoostPad;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

import java.awt.*;
import java.util.Comparator;
import java.util.Optional;

public class BoostPadNodeTest extends FlyveBot {

    public BoostPadNodeTest() {}

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        return new BotOutput();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        Optional<BoostPad> closest = BoostManager.boostPads.stream().min(new Comparator<BoostPad>() {
            @Override
            public int compare(BoostPad o1, BoostPad o2) {
                if(input.allCars.get(1-input.botIndex).position.minus(o1.location).magnitudeSquared() <
                        input.allCars.get(1-input.botIndex).position.minus(o2.location).magnitudeSquared()) {
                    return -1;
                }
                else if(input.allCars.get(1-input.botIndex).position.minus(o1.location).magnitudeSquared() >
                        input.allCars.get(1-input.botIndex).position.minus(o2.location).magnitudeSquared()) {
                    return 1;
                }
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
        closest.ifPresent(boostPad -> {
            for(BoostPad neighbour: boostPad.neighbours) {
                if(randomBooleanWithProbability(2)) {
                    renderer.drawLine3d(Color.CYAN, neighbour.location.plus(new Vector3(0, 0, 200)).toFlatVector(), boostPad.location.toFlatVector());
                }
            }
        });
    }

    private boolean randomBooleanWithProbability(double p) {
        return p > Math.random();
    }
}
