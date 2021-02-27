package rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.linear_transform.LinearApproximator;
import util.math.vector.Vector2;

public class AccelerationFunctionApproximator extends SkillController {

    private final BotBehaviour bot;

    private LinearApproximator accelerationApproximation;
    private double speedToReach;
    private double previousVelocity;
    private double velocity;
    private double acceleration;

    public AccelerationFunctionApproximator(BotBehaviour bot) {
        this.bot = bot;

        this.accelerationApproximation = new LinearApproximator();
        this.accelerationApproximation.sample(new Vector2(1, 2300));
        this.accelerationApproximation.sample(new Vector2(-1, -2300));
        this.speedToReach = 0;

        this.previousVelocity = 0;
        this.velocity = 0;
        this.acceleration = 0;
    }

    public void setSpeed(final double speedToReach) {
        this.speedToReach = speedToReach;
    }

    @Override
    public void updateOutput(DataPacket input) {
        previousVelocity = velocity;
        velocity = input.car.velocity.dotProduct(input.car.orientation.noseVector);
        acceleration = velocity - previousVelocity;

        // update the approximator
        double userInput = accelerationApproximation.inverse(speedToReach-velocity);

        accelerationApproximation.sample(new Vector2(userInput, acceleration));

        bot.output().throttle(userInput);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {

    }
}
