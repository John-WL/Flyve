package rlbotexample.input.prediction;

import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Ray3;
import util.math.vector.Vector3;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@FunctionalInterface
public interface Trajectory3D {

    Vector3 compute(double time);

    default Vector3 derivative(double time) {
        double h = 1.0/RawBallTrajectory.PREDICTION_REFRESH_RATE;
        Vector3 position = compute(time - h);
        if(position == null) {
            return null;
        }
        Vector3 result = compute(time).minus(position);
        result = result.scaled(1/h);

        return result;
    }

    static double findTimeOfClosestApproachBetween(Trajectory3D trajectory1, Trajectory3D trajectory2, double maxTime, double resolution) {
        double bestTime = 0;
        Vector3 initialPosition1 = trajectory1.compute(0);
        Vector3 initialPosition2 = trajectory2.compute(0);
        double bestDistanceBetweenTrajectories;
        if(initialPosition1 == null || initialPosition2 == null) {
            bestDistanceBetweenTrajectories = Double.MAX_VALUE;
        }
        else {
            bestDistanceBetweenTrajectories = initialPosition1.minus(trajectory2.compute(0)).magnitude();
        }

        for(int i = 1; i < maxTime*resolution; i++) {
            double testedTime = i/resolution;
            Vector3 position1 = trajectory1.compute(testedTime);
            Vector3 position2 = trajectory2.compute(testedTime);
            if(position1 == null || position2 == null) {
                continue;
            }
            double distanceBetweenTrajectories = position1.minus(position2).magnitude();
            if(distanceBetweenTrajectories < bestDistanceBetweenTrajectories) {
                bestTime = testedTime;
                bestDistanceBetweenTrajectories = distanceBetweenTrajectories;
            }
        }

        return bestTime;
    }

    /** finds the first non-null element
     *  worst case is O(duration * dt)
     * */
    default MovingPoint firstValid(final double amountOfTimeToSearch, final double precision) {
        MovingPoint movingPoint = null;
        Vector3 tempPoint;
        double tempTime;

        for(int i = 0; i < amountOfTimeToSearch/precision; i++) {
            tempTime = i*precision;
            tempPoint = compute(tempTime);

            if(tempPoint != null && derivative(tempTime) != null) {
                movingPoint = new MovingPoint(new Ray3(tempPoint, derivative(tempTime)), tempTime);
                break;
            }
        }

        return movingPoint;
    }

    /** lazy removal of some parts of the trajectory */
    default Trajectory3D remove(Function<MovingPoint, Boolean> isRemoved) {
        return time -> {
            Vector3 position = compute(time);
            if(position == null) {
                return null;
            }
            Vector3 velocity = derivative(time);
            if(velocity == null) {
                return null;
            }
            MovingPoint movingPosition = new MovingPoint(new Ray3(position, velocity), time);
            return isRemoved.apply(movingPosition) ? null : movingPosition.currentState.offset;
        };
    }

    /** lazy keeping of some parts of the trajectory */
    default Trajectory3D keep(Function<MovingPoint, Boolean> isKept) {
        return time -> {
            Vector3 position = compute(time);
            if(position == null) {
                return null;
            }
            Vector3 velocity = derivative(time);
            if(velocity == null) {
                return null;
            }
            MovingPoint movingPosition = new MovingPoint(new Ray3(position, velocity), time);
            return isKept.apply(movingPosition) ? movingPosition.currentState.offset : null;
        };
    }

    /** lazy modification of the trajectory */
    default Trajectory3D modify(Function<MovingPoint, Vector3> movingPointFunction) {
        return time -> {
            Vector3 position = compute(time);
            if(position == null) {
                return null;
            }
            Vector3 velocity = derivative(time);
            if(velocity == null) {
                return null;
            }
            MovingPoint movingPosition = new MovingPoint(new Ray3(position, velocity), time);
            return movingPointFunction.apply(movingPosition);
        };
    }

}
