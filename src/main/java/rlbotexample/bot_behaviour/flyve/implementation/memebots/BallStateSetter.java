package rlbotexample.bot_behaviour.flyve.implementation.memebots;

import rlbot.gamestate.*;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.car.HitBox;
import util.game_constants.RlConstants;
import util.game_situation.GameSituation;
import util.math.vector.Ray3;
import util.math.vector.Vector3;

public class BallStateSetter {

    public static void handleBallState(DataPacket input) {
        //BallStateSetter.setBallOutOfTheMap();

        for(ExtendedCarData car: input.allCars) {
            HitBox carHitBox = car.hitBox;
            Vector3 pointToConsiderForOrangeGoal = carHitBox.projectPointOnSurface(car.position.plus(new Vector3(0, -10000, 0)));
            Vector3 pointToConsiderForBlueGoal = carHitBox.projectPointOnSurface(car.position.plus(new Vector3(0, 10000, 0)));
            if(pointToConsiderForOrangeGoal.y > RlConstants.WALL_DISTANCE_Y
                    || pointToConsiderForBlueGoal.y < -RlConstants.WALL_DISTANCE_Y) {
                if(car.team == 0) {
                    BallStateSetter.setBallStateForOrangeWin();
                }
                else {
                    BallStateSetter.setBallStateForBlueWin();
                }
            }
        }

        BallStateSetter.setBallAsOtherPlayerCam(input.allCars.get(1-input.playerIndex), input.car);
    }

    public static void setBallStateForBlueWin() {
        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withBallState(new BallState(new PhysicsState().withLocation(new DesiredVector3(0f, 5560f, 200f))
                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                .withRotation(new DesiredRotation(0f, 0f, 0f))
                .withVelocity(new DesiredVector3(0f, 0f, 0f))));

        GameSituation.applyGameState(gameState);
    }

    public static void setBallStateForOrangeWin() {
        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withBallState(new BallState(new PhysicsState().withLocation(new DesiredVector3(0f, -5560f, 200f))
                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                .withRotation(new DesiredRotation(0f, 0f, 0f))
                .withVelocity(new DesiredVector3(0f, 0f, 0f))));

        GameSituation.applyGameState(gameState);
    }

    public static void setBallOutOfTheMap() {
        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withBallState(new BallState(new PhysicsState().withLocation(new DesiredVector3(0f, 0f, 2200f))
                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                .withRotation(new DesiredRotation(0f, 0f, 0f))
                .withVelocity(new DesiredVector3(0f, 0f, 0f))));

        GameSituation.applyGameState(gameState);
    }

    private static void setBallAsOtherPlayerCam(ExtendedCarData playerCar, ExtendedCarData focusCar) {
        Ray3 camera = new Ray3(playerCar.position, focusCar.position.minus(playerCar.position));
        Vector3 ballPosition = camera.offset.plus(camera.direction.scaledToMagnitude(30000));

        GameState gameState = GameSituation.getCurrentGameState();
        gameState.withBallState(new BallState(new PhysicsState().withLocation(new DesiredVector3(ballPosition.x, ballPosition.y, ballPosition.z))
                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                .withRotation(new DesiredRotation(0f, 0f, 0f))
                .withVelocity(new DesiredVector3(0f, 0f, 0f))));

        GameSituation.applyGameState(gameState);
    }
}
