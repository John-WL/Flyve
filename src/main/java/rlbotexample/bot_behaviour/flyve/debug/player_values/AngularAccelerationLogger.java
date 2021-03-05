package rlbotexample.bot_behaviour.flyve.debug.player_values;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;

public class AngularAccelerationLogger extends FlyveBot {

    Vector3 lastSpin;
    Vector3 spin;
    Vector3 localAngularAcceleration;

    double spinAmountAvg;

    boolean previousHasUsedSecondJump;
    boolean hasUsedSecondJump;

    public AngularAccelerationLogger() {
        lastSpin = new Vector3();
        spin = new Vector3();
        localAngularAcceleration = new Vector3();

        previousHasUsedSecondJump = false;
        hasUsedSecondJump = false;
    }

    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        lastSpin = spin;
        spin = input.allCars.get(1-input.playerIndex).spin.toFrameOfReference(input.allCars.get(1-input.playerIndex).orientation);
        localAngularAcceleration = spin.minus(lastSpin).scaled(RlConstants.BOT_REFRESH_RATE);
        System.out.println(localAngularAcceleration.z);

        spinAmountAvg = localAngularAcceleration.magnitude();
        previousHasUsedSecondJump = hasUsedSecondJump;
        hasUsedSecondJump = input.allCars.get(1-input.playerIndex).hasUsedSecondJump;
        if(hasUsedSecondJump && !previousHasUsedSecondJump) {
            System.out.println(localAngularAcceleration);
        }

        return output();
    }

    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);

        renderer.drawLine3d(Color.GREEN, localAngularAcceleration.scaled(100).plus(new Vector3(0, 0, 100)).toFlatVector(), new Vector3(0, 0, 100).toFlatVector());
    }
}
