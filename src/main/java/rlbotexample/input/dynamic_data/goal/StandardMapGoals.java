package rlbotexample.input.dynamic_data.goal;

import util.game_constants.RlConstants;
import util.math.vector.Vector3;

public class StandardMapGoals {

    private static GoalRegion blueGoal = new GoalRegion(
            new Vector3(893-RlConstants.BALL_RADIUS, 5120+RlConstants.BALL_RADIUS, 643-RlConstants.BALL_RADIUS),
            new Vector3(-893+RlConstants.BALL_RADIUS, 5120+RlConstants.BALL_RADIUS, RlConstants.BALL_RADIUS));
    private static GoalRegion orangeGoal = new GoalRegion(
            new Vector3(893-RlConstants.BALL_RADIUS, -5120-RlConstants.BALL_RADIUS, 643-RlConstants.BALL_RADIUS),
            new Vector3(-893+RlConstants.BALL_RADIUS, -5120-RlConstants.BALL_RADIUS, RlConstants.BALL_RADIUS));

    public static GoalRegion getAlly(int teamId) {
        if(teamId == 0) {
            return blueGoal;
        }
        else {
            return orangeGoal;
        }
    }

    public static GoalRegion getOpponent(int teamId) {
        if(teamId == 0) {
            return orangeGoal;
        }
        else {
            return blueGoal;
        }
    }

}
