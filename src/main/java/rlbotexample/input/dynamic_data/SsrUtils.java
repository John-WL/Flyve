package rlbotexample.input.dynamic_data;

import rlbotexample.bot_behaviour.flyve.implementation.memebots.alternative_physics.PhysicsOfSsr;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import util.game_constants.RlConstants;
import util.math.vector.Ray3;
import util.shapes.Sphere;

import java.util.ArrayList;
import java.util.List;

public class SsrUtils {

    private static final List<Boolean> isInBallFormForAllPlayers = new ArrayList<>();
    private static final List<Integer> counterForPlayerHitBoxState = new ArrayList<>();
    private static final double AMOUNT_OF_TIME_BEFORE_HIT_BOX_SHAPE_CHANGE = 2;
    private static final int AMOUNT_OF_FRAMES_BEFORE_CHANGING_STATE = (int)(AMOUNT_OF_TIME_BEFORE_HIT_BOX_SHAPE_CHANGE * RlConstants.BOT_REFRESH_RATE);

    public static boolean getIsInBallForm(int playerIndex) {
        for(int i = 0; i <= playerIndex; i++) {
            try {
                return isInBallFormForAllPlayers.get(playerIndex);
            }
            catch (Exception e) {
                isInBallFormForAllPlayers.add(false);
                try {
                    return isInBallFormForAllPlayers.get(playerIndex);
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();
    }

    public static void addToCounterForPlayerHitBoxStateUntilItsFilledEnough(int playerIndex) {
        for(int i = 0; i <= playerIndex; i++) {
            try {
                counterForPlayerHitBoxState.get(playerIndex);
            }
            catch (Exception e) {
                counterForPlayerHitBoxState.add(0);
                try {
                    counterForPlayerHitBoxState.get(playerIndex);
                }
                catch (Exception ignored) {}
            }
        }
    }

    public static void updateIsInBallForm(ExtendedCarData carData) {
        // that's slightly ugly... sorry, I don't really want to waste time on this...
        addToCounterForPlayerHitBoxStateUntilItsFilledEnough(carData.playerIndex);

        boolean isABall = isInBallFormForAllPlayers.get(carData.playerIndex);
        Ray3 collisionRay = StandardMapSplitMesh.getCollisionRayOrElse(new Sphere(carData.position, PhysicsOfSsr.PLAYERS_RADII*2.5), null);
        if(collisionRay != null) {
            // We're close to the map, so we should count
            // if the player is oriented correctly.
            if (collisionRay.direction.dotProduct(carData.orientation.roofVector) > 0) {
                int currentCounterValue = counterForPlayerHitBoxState.get(carData.playerIndex);
                counterForPlayerHitBoxState.set(carData.playerIndex, currentCounterValue + 1);
            }
            // Else, we just reset the counter
            else {
                counterForPlayerHitBoxState.set(carData.playerIndex, 0);
            }

            // if the counter is high enough, switch back to car mode
            if(counterForPlayerHitBoxState.get(carData.playerIndex) > AMOUNT_OF_FRAMES_BEFORE_CHANGING_STATE) {
                isInBallFormForAllPlayers.set(carData.playerIndex, false);
                counterForPlayerHitBoxState.set(carData.playerIndex, AMOUNT_OF_FRAMES_BEFORE_CHANGING_STATE);
            }
        }
        else {
            isInBallFormForAllPlayers.set(carData.playerIndex, true);
            counterForPlayerHitBoxState.set(carData.playerIndex, 0);
        }
    }
}
