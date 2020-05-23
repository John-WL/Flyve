package rlbotexample.input.prediction.ball;

import rlbotexample.input.dynamic_data.BallData;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import rlbotexample.input.prediction.BallStopper;
import rlbotexample.input.prediction.player.PlayerPredictedAerialTrajectory;
import util.game_constants.RlConstants;
import util.shapes.Sphere;
import util.vector.Ray3;

import java.util.ArrayList;
import java.util.List;

public class AdvancedBallPrediction {

    public final List<BallData> balls = new ArrayList<>();
    private final double amountOfAvailableTime;
    private final double refreshRate;
    private final BallData initialBall;
    private final List<CarData> initialCars;
    private final BallStopper ballStopper = new BallStopper();
    private final StandardMapSplitMesh standardMap = new StandardMapSplitMesh();

    public AdvancedBallPrediction(final BallData initialBall, final List<CarData> initialCars, final double amountOfAvailableTime, final double refreshRate) {
        this.initialBall = initialBall;
        this.initialCars = initialCars;
        this.amountOfAvailableTime = amountOfAvailableTime;
        this.refreshRate = refreshRate;
        loadCustomBallPrediction(amountOfAvailableTime);
    }

    public BallData ballAtTime(final double deltaTime) {
        if((int) (refreshRate * deltaTime) >= balls.size()) {
            return balls.get(balls.size() - 1);
        }
        return balls.get((int) (refreshRate * deltaTime));
    }

    private void loadCustomBallPrediction(final double amountOfPredictionTimeToLoad) {
        // clear the current ball path so we can load the next one
        balls.clear();
        balls.add(initialBall);

        // instantiate useful values
        BallData previousPredictedBall = initialBall;
        BallData predictedBall;
        List<CarData> previousPredictedCars = initialCars;
        List<CarData> predictedCars = new ArrayList<>();
        for(int i = 0; i < amountOfPredictionTimeToLoad*refreshRate; i++) {
            // handle aerial ball
            predictedBall = updateAerialBall(previousPredictedBall, 1/refreshRate);

            // update cars' positions
            for(CarData previousPredictedCar: previousPredictedCars) {
                predictedCars.add(updateAerialCar(previousPredictedCar, 1/refreshRate));
            }

            // bounce the ball off of cars
            // NOT YET IMPLEMENTED

            // handle ball bounces and roll
            final Ray3 rayNormal = standardMap.getCollisionRayOrElse(
                    new Sphere(previousPredictedBall.position, RlConstants.BALL_RADIUS),
                    new Ray3());
            if(!rayNormal.direction.isZero() && rayNormal.direction.dotProduct(previousPredictedBall.velocity) < 0) {
                predictedBall = updateBallBounceAndRoll(previousPredictedBall, rayNormal, 1/refreshRate);
            }
            else {
            }

            // stop the ball if it's rolling too slowly
            predictedBall = updateBallStopper(predictedBall, 1/refreshRate);

            // make sure to set the game time correctly (these are seconds from the in-game current frame)
            predictedBall = new BallData(predictedBall.position, predictedBall.velocity, predictedBall.spin, i/refreshRate);
            balls.add(predictedBall);
            previousPredictedBall = predictedBall;
        }
    }

    private BallData updateAerialBall(final BallData ballData, final double deltaTime) {
        final BallAerialTrajectory ballTrajectory = new BallAerialTrajectory(ballData);
        return ballTrajectory.compute(deltaTime);
    }

    private BallData updateBallBounceAndRoll(final BallData ball, final Ray3 rayNormal, final double deltaTime) {
        return new BallBounce(ball, rayNormal).compute(deltaTime);
    }

    private BallData updateBallStopper(final BallData ballData, final double deltaTime) {
        return ballStopper.compute(ballData, deltaTime);
    }

    private CarData updateAerialCar(final CarData carData, final double deltaTime) {
        final PlayerPredictedAerialTrajectory carTrajectory = new PlayerPredictedAerialTrajectory(carData);
        return carTrajectory.compute(deltaTime);
    }
}
