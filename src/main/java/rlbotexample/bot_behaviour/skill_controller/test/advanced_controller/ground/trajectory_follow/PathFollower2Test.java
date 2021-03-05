package rlbotexample.bot_behaviour.skill_controller.test.advanced_controller.ground.trajectory_follow;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.path_follower.PathFollower2;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.ground.trajectories.GroundTrajectoryFinder;
import rlbotexample.output.BotOutput;
import util.game_situation.trainning_pack.TrainingPack;
import util.math.vector.Ray3;

public class PathFollower2Test extends FlyveBot {

    private PathFollower2 pathFollower;
    private TrainingPack gameSituationHandler;

    public PathFollower2Test() {
        //gameSituationHandler = new CircularTrainingPack();
        //gameSituationHandler.add(new GroundDribbleSetup1());
        //gameSituationHandler.add(new GroundDribbleSetup2());
        pathFollower = new PathFollower2(this);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        //if(gameSituationHandler.canLoad(input)) {
            //gameSituationHandler.update();
        //}

        pathFollower.setPath(t -> GroundTrajectoryFinder.getRightTurningTrajectory(new Ray3(input.car.position, input.car.orientation.noseVector), input.car.orientation.roofVector, 2000).apply(t));
        pathFollower.updateOutput(input);

        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        pathFollower.debug(renderer, input);
    }
}
