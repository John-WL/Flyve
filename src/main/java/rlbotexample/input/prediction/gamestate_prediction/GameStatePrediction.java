package rlbotexample.input.prediction.gamestate_prediction;

import rlbotexample.input.dynamic_data.RlUtils;
import rlbotexample.input.dynamic_data.ball.BallData;
import rlbotexample.input.dynamic_data.car.CarData;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.dynamic_data.car.hit_box.HitBox;
import rlbotexample.input.geometry.StandardMapSplitMesh;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.BallAerialTrajectory;
import rlbotexample.input.prediction.gamestate_prediction.ball.BallBounce;
import rlbotexample.input.prediction.gamestate_prediction.ball.BallStopper;
import rlbotexample.input.prediction.gamestate_prediction.object_collisions.*;
import rlbotexample.input.prediction.gamestate_prediction.player.CarBounce;
import rlbotexample.input.prediction.gamestate_prediction.player.PlayerPredictedAerialTrajectory;
import util.game_constants.RlConstants;
import util.shapes.Sphere;
import util.math.vector.Ray3;
import util.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameStatePrediction {

    //long currentTime = 0;
    //double previousTime = 0;

    public final List<BallData> balls = new ArrayList<>();
    public final List<List<CarData>> cars = new ArrayList<>();
    private final double refreshRate;
    private final BallData initialBall;
    private final List<CarData> initialCars;
    private final rlbotexample.input.prediction.gamestate_prediction.ball.BallStopper ballStopper = new BallStopper();
    private final StandardMapSplitMesh standardMap = new StandardMapSplitMesh();
    private final List<Integer> nextBallBounceIndexes = new ArrayList<>();
    private final Integer[] carsHitOnBallTimeIndexes;

    public GameStatePrediction(final BallData initialBall, final List<CarData> initialCars, final double amountOfAvailableTime, final double refreshRate) {
        this.initialBall = initialBall;
        this.initialCars = initialCars;
        this.refreshRate = refreshRate;
        this.carsHitOnBallTimeIndexes = new Integer[initialCars.size()];
        loadCustomBallPrediction(amountOfAvailableTime);
    }

    public BallData ballAtTime(final double deltaTime) {
        if((int) (refreshRate * deltaTime) >= balls.size()) {
            return balls.get(balls.size() - 1);
        }
        else if((int) (refreshRate * deltaTime) < 0) {
            return balls.get(0);
        }
        return balls.get((int)(refreshRate * deltaTime));
    }

    public Trajectory3D ballAsTrajectory() {
        return time -> ballAtTime(time).position;
    }

    public BallData ballAtInterpolatedTime(final double deltaTime) {
        if((int) (refreshRate * deltaTime) >= balls.size()-1) {
            return balls.get(balls.size() - 1);
        }
        else if((int) (refreshRate * deltaTime) < 0) {
            return balls.get(0);
        }
        int flooredTime = (int)(refreshRate * deltaTime);
        double remainder = (refreshRate * deltaTime) - flooredTime;
        return interpolate(balls.get((int)(refreshRate * deltaTime)), balls.get((int)(refreshRate * deltaTime)+1), remainder);
    }

    private BallData interpolate(BallData ball1, BallData ball2, double t) {
        return new BallData(
                ball1.position.scaled(t).plus(ball2.position.scaled(1-t)),
                ball1.velocity.scaled(t).plus(ball2.velocity.scaled(1-t)),
                ball1.spin.scaled(t).plus(ball2.spin.scaled(1-t)),
                ball1.time+t);
    }

    public List<Double> ballBounceTimes() {
        final List<Double> timeList = new ArrayList<>();
        for (Integer nextBallBounceIndex : nextBallBounceIndexes) {
            timeList.add(balls.get(nextBallBounceIndex).time);
        }
        return timeList;
    }

    public double carBounceTimes(ExtendedCarData car) {
        List<CarData> carTrajectoryList = cars.stream()
                .map(l -> l.get(car.playerIndex))
                .collect(Collectors.toList());

        CarData previous;
        CarData now = carTrajectoryList.get(0);
        for(int i = 1; i < carTrajectoryList.size(); i++) {
            previous = now;
            now = carTrajectoryList.get(i);
            if(now.velocity.minus(previous.velocity).scaled(RlUtils.BALL_PREDICTION_REFRESH_RATE).magnitude()
                    > RlConstants.NORMAL_GRAVITY_STRENGTH*2) {
                return i/RlUtils.BALL_PREDICTION_REFRESH_RATE;
            }
        }

        return Double.MAX_VALUE;
    }

    public Ray3 findLandingNormal(ExtendedCarData car) {
        double landingTime = carBounceTimes(car);
        CarData landingCar = carsAtTime(landingTime).get(car.playerIndex);
        Vector3 landingPosition = landingCar.position;
        HitBox collisionHitBox = car.hitBox.generateHypotheticalHitBox(landingPosition);
        return StandardMapSplitMesh.STANDARD_MAP_MESH
                .collideWith(collisionHitBox);
    }

    public BallData nextBallBounceOnMap() {
        return ballAtTime(ballBounceTimes().get(0));
    }

    public double timeOfCollisionBetweenCarAndBall(final int playerIndex) {
        if(carsHitOnBallTimeIndexes[playerIndex] == null) {
            return Double.MAX_VALUE;
        }
        return balls.get(carsHitOnBallTimeIndexes[playerIndex]).time;
    }

    public List<CarData> carsAtTime(final double deltaTime) {
        if((int) (refreshRate * deltaTime) >= cars.size()) {
            return cars.get(cars.size() - 1);
        }
        else if((int) (refreshRate * deltaTime) < 0) {
            return cars.get(0);
        }
        return cars.get((int) (refreshRate * deltaTime));
    }

    private void loadCustomBallPrediction(final double amountOfPredictionTimeToLoad) {
        // clear the current ball path so we can load the next one
        balls.clear();
        balls.add(initialBall);
        cars.clear();
        cars.add(initialCars);

        //previousTime = System.currentTimeMillis()/1000.0;

        nextBallBounceIndexes.clear();

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
                final CarData notBouncedCar = updateAerialCar(previousPredictedCar, 1/refreshRate);
                final CarData predictedCar = updateCarBounceFromMap(notBouncedCar, 1/refreshRate);
                predictedCars.add(predictedCar);
            }

            // save the current ball to compute the car's bounce with it just after computing the ball itself
            final BallData savedPredictedBallForCarCollisions = predictedBall;

            // bounce the ball off of cars
            for(CarData predictedCar: predictedCars) {
                predictedBall = updateBallFromCollision(predictedBall, predictedCar);
            }

            // bounce the cars off of the saved ball
            final List<CarData> carListForSwap = new ArrayList<>();
            for(int j = 0; j < predictedCars.size(); j++) {
                carListForSwap.add(updateCarFromCollision(predictedCars.get(j), savedPredictedBallForCarCollisions, j, i, 1/refreshRate));
            }
            predictedCars = carListForSwap;

            // handle ball bounces and roll (ball stay unchanged if no collision)
            predictedBall = updateBallBounceAndRoll(predictedBall, 1/refreshRate, i);

            // stop the ball if it's rolling too slowly
            predictedBall = updateBallStopper(predictedBall, 1/refreshRate);

            // make sure to set the predicted game time correctly (these are seconds from the in-game current time frame)
            predictedBall = new BallData(predictedBall.position, predictedBall.velocity, predictedBall.spin, i/refreshRate);

            // save and reset the ball
            balls.add(predictedBall);
            previousPredictedBall = predictedBall;

            // save and reset the cars
            cars.add(predictedCars);
            previousPredictedCars = predictedCars;
            predictedCars = new ArrayList<>();
        }

        //if(System.currentTimeMillis() % 1000 <= 30) {
            //System.out.println((System.currentTimeMillis()/1000.0) - previousTime);
        //}
    }

    private BallData updateAerialBall(final BallData ballData, final double deltaTime) {
        final BallAerialTrajectory ballTrajectory = new BallAerialTrajectory(ballData);
        return ballTrajectory.compute(deltaTime);
    }

    private BallData updateBallBounceAndRoll(final BallData ball, final double deltaTime, final int predictedINdex) {
        final Ray3 rayNormal = standardMap.getCollisionRayOrElse(
                new Sphere(ball.position, RlConstants.BALL_RADIUS),
                new Ray3());

        if(!rayNormal.direction.isZero() && rayNormal.direction.dotProduct(ball.velocity) < 0) {
            nextBallBounceIndexes.add(predictedINdex);
            return new BallBounce(ball, rayNormal).compute(deltaTime);
        }

        return ball;
    }

    private BallData updateBallStopper(final BallData ballData, final double deltaTime) {
        return ballStopper.compute(ballData, deltaTime);
    }

    private CarData updateAerialCar(final CarData carData, final double deltaTime) {
        final PlayerPredictedAerialTrajectory carTrajectory = new PlayerPredictedAerialTrajectory(carData);
        return carTrajectory.compute(deltaTime);
    }

    // this might not work for wheel collisions...
    private CarData updateCarBounceFromMap(final CarData carData, final double deltaTime) {
        final Ray3 rayNormal = standardMap.getCollisionRayOrElse(carData.hitBox, new Ray3());

        if(!rayNormal.direction.isZero() && rayNormal.direction.dotProduct(carData.velocity) < 0) {
            return new CarBounce(carData, rayNormal).compute(deltaTime);
        }

        return carData;
    }

    /*
    private CarData updateGroundCar(final double deltaTime) {
        return null;
    }
    */

    private BallData updateBallFromCollision(final BallData ballData, final CarData carData) {
        return new BallCollisionWithCar4(ballData, carData).compute();
    }

    private CarData updateCarFromCollision(final CarData carData, final BallData ballData, final int playerIndex, final int predictedFrameCount, final double deltaTime) {
        final Vector3 carCenterHitBoxPosition = carData.hitBox.centerPositionOfHitBox;
        final Vector3 pointOnCarSurfaceTowardBall = carData.hitBox.closestPointOnSurface(ballData.position);
        final double specificCarRadiusWithRespectToBall = pointOnCarSurfaceTowardBall.minus(carCenterHitBoxPosition).magnitude();

        if(carCenterHitBoxPosition.minus(ballData.position).magnitude() < specificCarRadiusWithRespectToBall + RlConstants.BALL_RADIUS) {
            carsHitOnBallTimeIndexes[playerIndex] = predictedFrameCount;
            return new CarCollisionWithBall(carData, ballData).compute(deltaTime);
        }

        return carData;
    }

    private CarData updateCarFromCollision(final CarData carToUpdate, final CarData carToCollideWith, final double deltaTime) {
        return carToUpdate;
    }
}
