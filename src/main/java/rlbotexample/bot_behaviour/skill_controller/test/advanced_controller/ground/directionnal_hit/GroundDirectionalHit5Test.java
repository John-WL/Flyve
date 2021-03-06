package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.directionnal_hit;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_hit.GroundDirectionalHit5;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.goal.StandardMapGoals;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class GroundDirectionalHit5Test extends FlyveBot {

    private GroundDirectionalHit5 groundDirectionalHit;
    private Vector3 ballDestination;

    public GroundDirectionalHit5Test() {
        groundDirectionalHit = new GroundDirectionalHit5(this);
        ballDestination = new Vector3();
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        StandardMapGoals.getOpponent(input.team).closestPointOfBallOnSurface(input.ball.position)
        .ifPresent(v -> ballDestination = v);
        groundDirectionalHit.setBallDestination(ballDestination);
        groundDirectionalHit.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        groundDirectionalHit.debug(renderer, input);
    }
}
