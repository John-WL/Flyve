package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.ground_driving.GroundDirectionalHit4;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.prediction.Trajectory3D;
import rlbotexample.output.BotOutput;
import util.math.vector.Vector3;

public class GroundDirectionalHit4Test extends FlyveBot {

    private GroundDirectionalHit4 groundDirectionalHit;
    private Vector3 ballDestination;

    public GroundDirectionalHit4Test() {
        groundDirectionalHit = new GroundDirectionalHit4(this);
        ballDestination = new Vector3(0, -6000, 100);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        groundDirectionalHit.setDestinationTrajectory(
                new Trajectory3D() {
                      @Override
                      public Vector3 compute(double time) {
                          Vector3 ballPosition = input.statePrediction.ballAtTime(time).position;
                          //if(ballPosition.z < 200) {
                              return ballPosition;
                          //}
                          //return new Vector3(Double.NaN, Double.NaN, Double.NaN);
                      }
                }
        );
        groundDirectionalHit.setOrientationTrajectory(new Trajectory3D() {
            @Override
            public Vector3 compute(double time) {
                return ballDestination.minus(input.statePrediction.ballAtTime(time).position).normalized();
            }
        });

        groundDirectionalHit.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        groundDirectionalHit.debug(renderer, input);
    }
}
