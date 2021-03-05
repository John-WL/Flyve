package rlbotexample.input.dynamic_data.ground.controls_output_utils;

import rlbotexample.input.dynamic_data.ground.slope_samples.DecelerationDueToTurningFinder;
import rlbotexample.input.dynamic_data.ground.slope_samples.MaxAccelerationFromThrottleFinder;
import util.controllers.PulseDriveController;
import util.controllers.throttle_controller.IdleThrottleController;
import util.controllers.throttle_controller.ReverseThrottleController;
import util.game_constants.RlConstants;

public class BaseForwardThrottleControlsOutput {

    private final ReverseThrottleController reverseThrottleController;
    private final IdleThrottleController idleThrottleController;

    public BaseForwardThrottleControlsOutput() {
        this.reverseThrottleController = new ReverseThrottleController();
        this.idleThrottleController = new IdleThrottleController();
    }

    public Double apply(Double desiredAcceleration, Double velocity, Double spin) {
        if(desiredAcceleration == 0 && Math.abs(spin) < 0.01) {
            if(velocity == 0) {
                return 0.0;
            }
            if(velocity > 0) {
                return 0.01;
            }
            else {
                return -0.01;
            }
        }
        else if(desiredAcceleration == 0) {
            final double compensationAccelerationForTurning = -DecelerationDueToTurningFinder.compute(velocity, spin);
            return compensationAccelerationForTurning / findMaxForwardAcceleration(velocity, spin);
        }
        if(Math.abs(velocity) < 5) {
            velocity = 0.0;
        }
        if(velocity * desiredAcceleration >= 0) {
            if(Math.abs(desiredAcceleration) > 3500) {
                return 2*desiredAcceleration/Math.abs(desiredAcceleration);
            }
            return desiredAcceleration / findMaxForwardAcceleration(Math.abs(velocity), spin);
        }
        else {
            if(reverseThrottleController.process(Math.abs(desiredAcceleration))) {
                return desiredAcceleration/Math.abs(desiredAcceleration);
            }
            if(idleThrottleController.process(desiredAcceleration)) {
                // update the reverse controller too (we are applying an acceleration but aren't using the controller, we need to tell it so)
                reverseThrottleController.process(-525);
                return 0.0;
            }
            if(velocity > 0) {
                return 0.01;
            }
            else {
                return -0.01;
            }
        }
    }

    public Double findMaxForwardAcceleration(Double velocity, Double spin) {
        final double maxThrottleAcceleration = MaxAccelerationFromThrottleFinder.compute(velocity);
        //final double decelerationDueToTurning = DecelerationDueToTurningFinder.compute(velocity, spin);
        return maxThrottleAcceleration;// + decelerationDueToTurning;
    }
}
