package rlbotexample.input.prediction;

import rlbotexample.input.dynamic_data.BallData;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.dynamic_data.KinematicCar;
import rlbotexample.input.geometry.StandardMap;
import util.game_constants.RlConstants;
import util.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class AdvancedBallPrediction {

    public final List<BallData> balls = new ArrayList<>();
    private final double amountOfAvailableTime;
    private final double refreshRate;
    private final BallData initialBall;
    private final List<CarData> initialCars;
    private final SlidingBall slidingBallTrajectory = new SlidingBall();

    public AdvancedBallPrediction(final BallData initialBall, final List<CarData> initialCars, final double amountOfAvailableTime, final double refreshRate) {
        this.initialBall = initialBall;
        this.initialCars = initialCars;
        this.amountOfAvailableTime = amountOfAvailableTime;
        this.refreshRate = refreshRate;
        loadCustomBallPrediction(amountOfAvailableTime);
    }

    public BallData ballAtTime(final double deltaTime) {
        return balls.get((int) (refreshRate * deltaTime));
    }

    private void loadCustomBallPrediction(final double amountOfPredictionTimeToLoad) {
        // clear the current ball path so we can load the next one
        balls.clear();

        // instantiate useful values
        BallData previousPredictedBall = initialBall;
        final List<KinematicCar> predictedCars = new ArrayList<>();
        for (final CarData initialCar : initialCars) {
            predictedCars.add(new KinematicCar(initialCar.position, initialCar.velocity, initialCar.spin, initialCar.hitBox, 0));
        }
        final StandardMap standardMap = new StandardMap();

        for(int i = 0; i < amountOfPredictionTimeToLoad*refreshRate; i++) {
            // try to step 1 frame into the future
            BallData predictedBall = updateAerialBall(previousPredictedBall, 1/refreshRate);
            System.out.println(predictedBall.velocity.z);

            // handle map bounces
            final Vector3 ballToMapHitNormal = standardMap.getHitNormal(predictedBall.position, RlConstants.BALL_RADIUS);
            if(!ballToMapHitNormal.isZero()) {
                predictedBall = updateBallBounce(previousPredictedBall, ballToMapHitNormal);
            }

            // make sure to set the game time correctly (these are seconds from the current frame)
            predictedBall = new BallData(predictedBall.position, predictedBall.velocity, predictedBall.spin, i/refreshRate);
            balls.add(predictedBall);
            previousPredictedBall = predictedBall;
        }
    }

    private BallData updateAerialBall(final BallData ballData, final double deltaTime) {
        final Vector3 gravity = new Vector3(0, 0, -RlConstants.NORMAL_GRAVITY_STRENGTH);
        final Parabola3D ballTrajectory = new Parabola3D(ballData.position, ballData.velocity, gravity);
        ballTrajectory.setAirDrag(RlConstants.BALL_AIR_DRAG_COEFFICIENT);
        final Vector3 updatedPosition = ballTrajectory.compute(deltaTime);
        final Vector3 updatedSpeed = ballTrajectory.derivative(deltaTime);
        final Vector3 updatedSpin = ballData.spin;

        return new BallData(updatedPosition, updatedSpeed, updatedSpin, deltaTime);
    }

    private BallData updateSlidingBall(final BallData ballData, final Vector3 surfaceNormal, final double deltaTime) {
        return slidingBallTrajectory.compute(ballData, surfaceNormal, deltaTime);
    }

    private BallData updateBallBounce(final BallData ball, final Vector3 hitNormal) {
        return new BallBounce(ball, hitNormal).compute();
    }
}
