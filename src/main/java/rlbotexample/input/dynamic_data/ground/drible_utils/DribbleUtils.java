package rlbotexample.input.dynamic_data.ground.drible_utils;

import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.input.prediction.gamestate_prediction.ball.RawBallTrajectory;
import util.game_constants.RlConstants;
import util.math.vector.MovingPoint;
import util.math.vector.Vector3;

public class DribbleUtils {

    private static final double MAX_DISTANCE_FROM_CAR_ROOF = 180;
    private static final double BALL_VELOCITY_Z_LIMIT_FOR_CONTROLLED_DRIBBLE = 300;
    private static final double BALL_VELOCITY_Z_LIMIT_FOR_SWIPE = 200;
    private static final double BALL_POSITION_Z_LIMIT_FOR_SWIPE = 250;

    private static final double ARBITRARY_FACTOR_TO_PROJECT_VECTOR_ON_CAR_ROOF = 100;

    public static boolean botInControl(DataPacket input, int playerIndex) {
        ExtendedCarData car = input.allCars.get(playerIndex);

        Vector3 lowestPointOfBall = input.ball.position
                .plus(Vector3.DOWN_VECTOR.scaled(RlConstants.BALL_RADIUS));
        Vector3 highestPointOnRoofOfCarHitBox = car.hitBox.closestPointOnSurface(car.position
                .plus(car.orientation.roofVector.scaled(ARBITRARY_FACTOR_TO_PROJECT_VECTOR_ON_CAR_ROOF)));

        return lowestPointOfBall.minus(highestPointOnRoofOfCarHitBox).magnitude() < MAX_DISTANCE_FROM_CAR_ROOF
                && Math.abs(input.ball.velocity.z) < BALL_VELOCITY_Z_LIMIT_FOR_CONTROLLED_DRIBBLE
                && car.hasWheelContact;
    }

    public static boolean botInControl(DataPacket input) {
        return botInControl(input, input.playerIndex);
    }

    // a "swipe" is the designation for the action of hitting the ball with the side of the car repetitively
    // and hard enough so that the ball lifts from the ground and moves onto the car's roof
    public static boolean ballBouncingTooMuchForSwipe(DataPacket input) {
        // remove all the balls that are considered "bouncy"
        Trajectory3D trimmedBallTrajectory = RawBallTrajectory.trajectory
                .remove(movingPoint -> movingPoint.physicsState.offset.z > BALL_POSITION_Z_LIMIT_FOR_SWIPE
                        || Math.abs(movingPoint.physicsState.direction.z) > BALL_VELOCITY_Z_LIMIT_FOR_SWIPE);
        MovingPoint firstValidMovingPoint = trimmedBallTrajectory
                .first(
                        input.car.position.minus(input.ball.position).magnitude()/RlConstants.CAR_MAX_SPEED,
                        1.0/RawBallTrajectory.PREDICTION_REFRESH_RATE);

        return firstValidMovingPoint == null;
    }
}
