package rlbotexample.game_state;

public class GoalHandler {

    private static int currentGoalsDifference = 0;
    private static int lastFrameGoalsDifference = 0;
    private static int goalsDifferenceOnIterationStart = 0;

    public static void startNewIteration() {
        // update the number of goals for the new iteration
        goalsDifferenceOnIterationStart = currentGoalsDifference;
    }

    public static int getGoalsDifference() {
        return currentGoalsDifference - goalsDifferenceOnIterationStart;
    }

    public static boolean goalScoreEvent() {
        return currentGoalsDifference - lastFrameGoalsDifference != 0;
    }

    public static void update(int newGoalsDifference) {
        lastFrameGoalsDifference = currentGoalsDifference;
        currentGoalsDifference = newGoalsDifference;
    }
}
