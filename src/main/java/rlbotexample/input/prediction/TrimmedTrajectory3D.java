package rlbotexample.input.prediction;

import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.math.discrete_utils.DiscreteDuration;
import util.math.vector.MovingPoint;
import util.math.vector.Ray3;
import util.math.vector.Vector3;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@FunctionalInterface
public interface TrimmedTrajectory3D extends Function<Double, Optional<Vector3>> {

    double h = 2.0/RawBallTrajectory.PREDICTION_REFRESH_RATE;

    default Optional<Vector3> derivative(double time) {
        return apply(time - h).flatMap(position1 -> apply(time).map(position2 ->
                position2.minus(position1).scaled(1/h)));
    }

    static Optional<Double> findTimeOfClosestApproach(TrimmedTrajectory3D trajectory1, TrimmedTrajectory3D trajectory2, DiscreteDuration discreteDuration) {
        AtomicReference<Optional<Double>> resultOptRef = new AtomicReference<>(Optional.empty());
        AtomicReference<Optional<Double>> bestDistanceSquaredOptRef = new AtomicReference<>(Optional.empty());
        List<Optional<MovingPoint>> quantizedTrajectory1 = trajectory1.quantize(discreteDuration);
        List<Optional<MovingPoint>> quantizedTrajectory2 = trajectory2.quantize(discreteDuration);

        discreteDuration.forEach((i, time) -> {
            Optional<MovingPoint> movingPointOpt1 = quantizedTrajectory1.get(i);
            Optional<MovingPoint> movingPointOpt2 = quantizedTrajectory2.get(i);

            movingPointOpt1.ifPresent(movingPoint1 -> movingPointOpt2.ifPresent(movingPoint2 -> {
                Double distanceSquared = movingPoint1.physicsState.offset
                        .distanceSquared(movingPoint2.physicsState.offset);
                if(resultOptRef.get().isPresent() && bestDistanceSquaredOptRef.get().isPresent()) {
                    if(bestDistanceSquaredOptRef.get().get() > distanceSquared) {
                        bestDistanceSquaredOptRef.set(Optional.of(distanceSquared));
                        resultOptRef.set(Optional.of(time));
                    }
                }
                else {
                    resultOptRef.set(Optional.of(time));
                    bestDistanceSquaredOptRef.set(Optional.of(distanceSquared));
                }
            }));
            return null;
        });

        return resultOptRef.get();
    }

    default Optional<MovingPoint> first(DiscreteDuration discreteDuration) {
        return quantize(discreteDuration)
                .stream()
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get);
    }

    default List<Optional<MovingPoint>> quantize(DiscreteDuration discreteDuration) {
        List<Optional<MovingPoint>> points = new ArrayList<>();
        discreteDuration.forEach((i, time) -> {
            points.add(apply(time).flatMap(position -> derivative(time).map(velocity ->
                    new MovingPoint(new Ray3(position, velocity), time))));
            return null;
        });
        return points;
    }

    default TrimmedTrajectory3D remove(Function<MovingPoint, Boolean> isRemoved) {
        return time -> apply(time).flatMap(position -> derivative(time).flatMap(velocity -> {
            MovingPoint movingPoint = new MovingPoint(new Ray3(position, velocity), time);
            return isRemoved.apply(movingPoint) ? Optional.empty() : Optional.of(position);
        }));
    }

    default TrimmedTrajectory3D keep(Function<MovingPoint, Boolean> isKept) {
        return time -> apply(time).flatMap(position -> derivative(time).flatMap(velocity -> {
            MovingPoint movingPoint = new MovingPoint(new Ray3(position, velocity), time);
            return isKept.apply(movingPoint) ? Optional.of(position) : Optional.empty();
        }));
    }

    default TrimmedTrajectory3D modify(Function<MovingPoint, Vector3> movingPointFunction) {
        return time -> apply(time).flatMap(position -> derivative(time).map(velocity -> {
            MovingPoint movingPoint = new MovingPoint(new Ray3(position, velocity), time);
            return movingPointFunction.apply(movingPoint);
        }));
    }

    default void forEach(DiscreteDuration discreteDuration, Function<MovingPoint, Void> iterationFunction) {
        discreteDuration.forEach((i, time) -> {
            apply(time).flatMap(position -> derivative(time).map(velocity -> {
                MovingPoint movingPoint = new MovingPoint(new Ray3(position, velocity), time);
                iterationFunction.apply(movingPoint);
                return null;
            }));
            return null;
        });
    }
}
