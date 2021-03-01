package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.types;

import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationController5;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.jump.JumpType;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.orientation.CarOrientation;
import rlbotexample.output.BotOutput;
import util.game_constants.RlConstants;
import util.math.vector.Ray3;
import util.math.vector.Vector;
import util.math.vector.Vector2;
import util.math.vector.Vector3;

public class WaveDash extends JumpType {

    private static final double REMAINING_DISTANCE_BETWEEN_CHASSIS_AND_GROUND_WHEN_DASHING = 20;
    private static final double DASH_ANGLE_FROM_NORMAL = Math.PI/5;

    private final AerialOrientationController5 aerialOrientationController5;
    private Vector3 overwritingFrontOrientation;

    public WaveDash(BotBehaviour bot, DataPacket input) {
        super(jumpDuration(input));
        this.aerialOrientationController5 = new AerialOrientationController5(bot);
        this.overwritingFrontOrientation = new Vector3();
    }

    public void setDesiredFrontOrientation(Vector3 frontOrientation) {
        overwritingFrontOrientation = frontOrientation;
    }

    @Override
    public void jump(DataPacket input, BotOutput output, Vector3 dashDirection) {
        setJumpDuration(jumpDuration(input));
        updateCurrentJumpCallCounter();

        Ray3 hitNormal = input.statePrediction.findLandingNormal(input.car);
        Vector3 desiredFrontOrientation = input.car.orientation.noseVector.minus(input.car.orientation.noseVector
                        .projectOnto(hitNormal.direction)).normalized();
        if(!overwritingFrontOrientation.isZero()) {
            desiredFrontOrientation = overwritingFrontOrientation.minus(input.car.position)
                    .minus(overwritingFrontOrientation.minus(input.car.position)
                            .projectOnto(hitNormal.direction)).normalized();
        }
        Vector3 globalDashDestination = dashDirection.scaled(-1, 1, 1)
                .matrixRotation(input.car.orientation).plus(input.car.position);
        Vector3 predictedDashDirectionUponLanding = globalDashDestination.minus(hitNormal.offset);
        Vector3 orientationRotator = predictedDashDirectionUponLanding.crossProduct(hitNormal.direction)
                .scaledToMagnitude(DASH_ANGLE_FROM_NORMAL);
        CarOrientation desiredOrientation = new CarOrientation(desiredFrontOrientation, hitNormal.direction);
        desiredOrientation = desiredOrientation.rotate(orientationRotator);
        aerialOrientationController5.setOrientation(desiredOrientation);
        aerialOrientationController5.updateOutput(input);

        if(input.car.position.minus(input.car.orientation.roofVector.scaled(RlConstants.OCTANE_POSITION_ELEVATION_WHEN_DRIVING)).minus(hitNormal.offset).magnitude()
                < REMAINING_DISTANCE_BETWEEN_CHASSIS_AND_GROUND_WHEN_DASHING
                && input.car.velocity.normalized()
                .dotProduct(hitNormal.offset.minus(input.car.position).normalized()) > 0) {
            Vector2 flipDirections = dashDirection.flatten().normalized();
            output.pitch(flipDirections.x);
            output.yaw(flipDirections.y);
            output.roll(0);
            output.jump(true);
        }
    }

    private static int jumpDuration(DataPacket input) {
        return (int)(input.statePrediction.carBounceTimes(input.car) / RlConstants.BOT_REFRESH_RATE);
    }
}
