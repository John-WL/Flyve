package rlbotexample.input.prediction;

import util.game_constants.RlConstants;
import util.math.vector.Vector3;

@FunctionalInterface
public interface Trajectory3D {

    Vector3 compute(double time);

    default Vector3 derivative(double time) {
        double h = 1/RlConstants.BOT_REFRESH_RATE;
        Vector3 result = compute(time + h).minus(compute(time));
        result = result.scaled(1/h);

        return result;
    }

    static double findTimeOfClosestApproachBetween(Trajectory3D trajectory1, Trajectory3D trajectory2, double maxTime, double resolution) {
        double bestTime = 0;
        double bestDistanceBetweenTrajectories = trajectory1.compute(0).minus(trajectory2.compute(0)).magnitude();

        for(int i = 1; i < maxTime*resolution; i++) {
            double testedTime = i/resolution;
            Vector3 position1 = trajectory1.compute(testedTime);
            Vector3 position2 = trajectory2.compute(testedTime);
            double distanceBetweenTrajectories = position1.minus(position2).magnitude();
            if(distanceBetweenTrajectories < bestDistanceBetweenTrajectories) {
                bestTime = testedTime;
                bestDistanceBetweenTrajectories = distanceBetweenTrajectories;
            }
        }

        return bestTime;
    }
}
