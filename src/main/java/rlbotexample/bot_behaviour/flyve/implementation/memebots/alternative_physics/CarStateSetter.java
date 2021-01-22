package rlbotexample.bot_behaviour.flyve.implementation.memebots.alternative_physics;

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
                        .withVelocity(new DesiredVector3(-newVelocity.x, newVelocity.y, newVelocity.z)))
                );
    }

    public static void moveBy(ExtendedCarData carData, Vector3 delta) {
        Vector3 newPosition = carData.position.plus(delta);

        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withCarState(carData.playerIndex, new CarState()
                .withPhysics(new PhysicsState()
                        .withLocation(new DesiredVector3(-newPosition.x, newPosition.y, newPosition.z)))
        );
    }

    public static void addImpulseAndMoveBy(ExtendedCarData carData, Vector3 impulse, Vector3 delta) {
        Vector3 newVelocity = carData.velocity.plus(impulse);
        Vector3 newPosition = carData.position.plus(delta);

        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withCarState(carData.playerIndex, new CarState()
                .withPhysics(new PhysicsState()
                        .withVelocity(new DesiredVector3(-newVelocity.x, newVelocity.y, newVelocity.z))
                        .withLocation(new DesiredVector3(-newPosition.x, newPosition.y, newPosition.z)))
        );

        GameSituation.applyGameState(gameState);
    }

    public static void set(ExtendedCarData carData, Vector3 impulse, Vector3 delta, double boostAmount) {
        Vector3 newVelocity = carData.velocity.plus(impulse);
        Vector3 newPosition = carData.position.plus(delta);

        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withCarState(carData.playerIndex, new CarState()
                .withPhysics(new PhysicsState()
                        .withVelocity(new DesiredVector3(-newVelocity.x, newVelocity.y, newVelocity.z))
                        .withLocation(new DesiredVector3(-newPosition.x, newPosition.y, newPosition.z)))
                .withBoostAmount((float)boostAmount)
        );

        GameSituation.applyGameState(gameState);
    }
}
