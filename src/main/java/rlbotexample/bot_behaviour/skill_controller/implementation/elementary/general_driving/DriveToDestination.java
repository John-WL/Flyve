package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.aerial_orientation.AerialOrientationHandler;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class DriveToDestination extends SkillController {

    private final BotBehaviour bot;
    private final DrivingSpeedController drivingSpeedController;
    private final GroundOrientationController groundOrientationController;
    private final AerialOrientationHandler aerialOrientationHandler;
    private double speedToReach;
    private Vector3 destination;

    public DriveToDestination(BotBehaviour bot) {
        this.bot = bot;
        this.drivingSpeedController = new DrivingSpeedController(bot);
        this.groundOrientationController = new GroundOrientationController(bot);
        this.aerialOrientationHandler = new AerialOrientationHandler(bot);
        this.speedToReach = 1410;
    }

    public void setDestination(final Vector3 destination) {
        this.destination = destination;
    }

    public void setSpeed(final double speedToReach) {
        this.speedToReach = speedToReach;
    }

    @Override
    public void updateOutput(DataPacket input) {
        BotOutput output = bot.output();
        drivingSpeedController.setSpeed(speedToReach);
        groundOrientationController.setDestination(destination);
        aerialOrientationHandler.setDestination(destination);
        aerialOrientationHandler.setRollOrientation(new Vector3(0, 0, 10000));

        drivingSpeedController.updateOutput(input);
        groundOrientationController.updateOutput(input);
        aerialOrientationHandler.updateOutput(input);
        output.boost(speedToReach > 1410 && input.car.velocity.magnitude() < speedToReach && input.car.orientation.noseVector.dotProduct(destination.minus(input.car.position).normalized()) > 0);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {

    }
}
