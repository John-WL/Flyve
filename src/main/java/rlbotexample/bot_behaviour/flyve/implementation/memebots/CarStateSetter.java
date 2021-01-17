package rlbotexample.bot_behaviour.flyve.implementation.memebots;

import rlbot.gamestate.*;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.car.HitBox;
import util.game_constants.RlConstants;
import util.game_situation.GameSituation;
import util.math.vector.Vector3;

public class CarStateSetter {

    public static void addImpulse(ExtendedCarData carData, Vector3 impulse) {
        Vector3 newVelocity = carData.velocity.plus(impulse);

        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withCarState(carData.playerIndex, new CarState()
                .withPhysics(new PhysicsState()
                        .withVelocity(new DesiredVector3(newVelocity.x, newVelocity.y, newVelocity.z)))
                );

        GameSituation.applyGameState(gameState);
    }
}
