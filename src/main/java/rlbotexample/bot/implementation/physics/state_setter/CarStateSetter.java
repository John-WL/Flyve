package rlbotexample.bot.implementation.physics.state_setter;

import rlbot.gamestate.*;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.dynamic_data.car.orientation.CarOrientation;
import util.game_constants.RlConstants;
import util.game_situation.GameSituation;
import util.math.vector.Vector;
import util.math.vector.Vector3;

public class CarStateSetter {

    public static void moveTo(Vector3 position, Vector3 orientation, ExtendedCarData carData) {
        Vector3 previousPosition = carData.position;
        Vector3 generatedVelocity = position.minus(previousPosition).scaled(RlConstants.BOT_REFRESH_RATE);

        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withCarState(carData.playerIndex, new CarState()
                .withPhysics(new PhysicsState()
                        .withLocation(new DesiredVector3((float)-position.x, (float)position.y, (float)position.z))
                        .withVelocity(new DesiredVector3((float)-generatedVelocity.x, (float)generatedVelocity.y, (float)generatedVelocity.z))
                        .withRotation(new DesiredRotation((float)orientation.y, (float)-orientation.z, (float)orientation.x))
                ));
        GameSituation.applyGameState(gameState);
    }
}