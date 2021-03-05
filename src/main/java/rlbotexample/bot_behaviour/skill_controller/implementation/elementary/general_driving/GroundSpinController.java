package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.controls_output_utils.BaseSteeringControlsOutput;
import util.controllers.DriftController;

public class GroundSpinController extends SkillController {

    private static final double DELTA_SPIN_FACTOR = 2;

    private final BotBehaviour bot;
    private double desiredSpin;
    private DriftController driftController;

    public GroundSpinController(BotBehaviour bot) {
        this.bot = bot;
        this.desiredSpin = 0;
        this.driftController = new DriftController();
    }

    public void setSpin(final double desiredSpin) { this.desiredSpin = desiredSpin; }

    @Override
    public void updateOutput(DataPacket input) {
        double steerAmount = findSteerAmount(input);
        bot.output().steer(steerAmount);

        double driftAmount = findDriftAmount(steerAmount, input);
        boolean driftState = driftController.process(driftAmount);
        bot.output().drift(driftState);
    }

    private double findSteerAmount(DataPacket input) {
        double carSpeed = input.car.velocity.magnitude();
        double baseSteer = BaseSteeringControlsOutput.apply(desiredSpin, carSpeed);

        double currentSpin = input.car.spin.toFrameOfReference(input.car.orientation).z;
        double deltaSpin = desiredSpin - currentSpin;
        double deltaSteer = deltaSpin * DELTA_SPIN_FACTOR;

        return baseSteer + deltaSteer;
    }

    private double findDriftAmount(double steerAmount, DataPacket input) {
        double carSpeed = input.car.velocity.magnitude();
        double currentSpin = input.car.spin.toFrameOfReference(input.car.orientation).z;

        if(Math.abs(steerAmount) > 1
                && Math.abs(currentSpin) > BaseSteeringControlsOutput.findMaxSpin(carSpeed) * 0.5) {
            return (Math.abs(steerAmount)-1)*0.4;
        }

        return 0;
    }

    @Override
    public void setupController() {}

    @Override
    public void debug(Renderer renderer, DataPacket input) {}
}
