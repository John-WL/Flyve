package rlbotexample.input.dynamic_data;

import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.prediction.gamestate_prediction.GameStatePrediction;
import util.game_constants.RlConstants;
import util.math.MovingAverage;
import util.timer.Timer;
import util.math.vector.Vector3;

import java.util.*;
import java.util.List;

public class RlUtils {

    public static final double BALL_PREDICTION_TIME = 5;
    public static final double BALL_PREDICTION_REFRESH_RATE = 120;

    private static Timer ballPredictionReloadTimeout = new Timer(0).start();
    private static GameStatePrediction ballPrediction = new GameStatePrediction(new BallData(new Vector3(), new Vector3(), new Vector3(), 0), new ArrayList<CarData>(), 0, BALL_PREDICTION_REFRESH_RATE);

    private static final List<Integer> amountOfFramesSinceFirstJumpOccurredForAllPlayers = new ArrayList<>();
    private static final List<Double> previousBoostAmountForAllPlayers = new ArrayList<>();
    private static final List<MovingAverage> averageBoostUsageForAllPlayers = new ArrayList<>();
    private static final List<Boolean> previousSecondJumpUsageForAllPlayers = new ArrayList<>();
    private static final int amountOfFramesForTheAverage = (int) (0.2*RlConstants.BOT_REFRESH_RATE);

    public static GameStatePrediction gameStatePrediction(int playerIndex, BallData ballData, List<CarData> allCars) {
        if(playerIndex == 0 && ballPredictionReloadTimeout.isTimeElapsed()) {
            ballPredictionReloadTimeout = new Timer(1.0/RlConstants.BOT_REFRESH_RATE).start();
            ballPrediction = new GameStatePrediction(ballData, allCars, BALL_PREDICTION_TIME, BALL_PREDICTION_REFRESH_RATE);
        }
        return ballPrediction;
    }

    public static int getPreviousAmountOfFramesSinceFirstJumpOccurred(int playerIndex) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                return amountOfFramesSinceFirstJumpOccurredForAllPlayers.get(playerIndex);
            } catch (Exception e) {
                amountOfFramesSinceFirstJumpOccurredForAllPlayers.add(0);
                try {
                    return amountOfFramesSinceFirstJumpOccurredForAllPlayers.get(playerIndex);
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();
    }

    public static void setPreviousAmountOfFramesSinceFirstJumpOccurred(int playerIndex, int framesSinceFirstJumpOccurred) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                amountOfFramesSinceFirstJumpOccurredForAllPlayers.set(playerIndex, framesSinceFirstJumpOccurred);
                return;
            } catch (Exception e) {
                amountOfFramesSinceFirstJumpOccurredForAllPlayers.add(0);
                try {
                    amountOfFramesSinceFirstJumpOccurredForAllPlayers.set(playerIndex, framesSinceFirstJumpOccurred);
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();
    }

    public static double getPreviousAmountOfBoost(int playerIndex) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                return previousBoostAmountForAllPlayers.get(playerIndex);
            }
            catch (Exception e) {
                previousBoostAmountForAllPlayers.add(0d);
                try {
                    return previousBoostAmountForAllPlayers.get(playerIndex);
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();
    }

    public static void setPreviousAmountOfBoost(int playerIndex, double boostAmount) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                averageBoostUsageForAllPlayers.get(playerIndex)
                        .update(
                                previousBoostAmountForAllPlayers.get(playerIndex) - boostAmount < -1 ?
                                RlConstants.ACCELERATION_DUE_TO_BOOST : 0
                        );
                previousBoostAmountForAllPlayers.set(playerIndex, boostAmount);
                return;
            } catch (Exception e) {
                averageBoostUsageForAllPlayers.add(new MovingAverage(amountOfFramesForTheAverage));
                previousBoostAmountForAllPlayers.add(0d);
                try {
                    averageBoostUsageForAllPlayers.get(playerIndex)
                            .update(
                                    previousBoostAmountForAllPlayers.get(playerIndex) - boostAmount < -1 ?
                                    RlConstants.ACCELERATION_DUE_TO_BOOST : 0
                            );
                    previousBoostAmountForAllPlayers.set(playerIndex, boostAmount);
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();
    }

    public static double getAverageBoostUsage(int playerIndex) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                return averageBoostUsageForAllPlayers.get(playerIndex).value;
            }
            catch (Exception e) {
                averageBoostUsageForAllPlayers.add(new MovingAverage(amountOfFramesForTheAverage));
                try {
                    return averageBoostUsageForAllPlayers.get(playerIndex).value;
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();

    }

    public static boolean getPreviousSecondJumpUsage(int playerIndex) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                return previousSecondJumpUsageForAllPlayers.get(playerIndex);
            }
            catch (Exception e) {
                previousSecondJumpUsageForAllPlayers.add(false);
                try {
                    return previousSecondJumpUsageForAllPlayers.get(playerIndex);
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();
    }

    public static void setPreviousSecondJumpUsage(int playerIndex, boolean hasUsedSecondJump) {
        for(int i = 0; i <= playerIndex; i++){
            try {
                previousSecondJumpUsageForAllPlayers.set(playerIndex, hasUsedSecondJump);
                return;
            } catch (Exception e) {
                previousSecondJumpUsageForAllPlayers.add(false);
                try {
                    previousSecondJumpUsageForAllPlayers.set(playerIndex, hasUsedSecondJump);
                }
                catch (Exception ignored) {}
            }
        }
        throw new RuntimeException();
    }
}
