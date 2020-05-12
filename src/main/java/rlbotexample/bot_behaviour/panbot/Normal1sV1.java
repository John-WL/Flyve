package rlbotexample.bot_behaviour.panbot;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.DriveToDestination;
import rlbotexample.bot_behaviour.skill_controller.Flick;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.Dribble;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.metagame.PossessionEvaluator;
import rlbotexample.bot_behaviour.path.BallPositionPath;
import rlbotexample.bot_behaviour.path.EnemyNetPositionPath;
import rlbotexample.bot_behaviour.path.PathHandler;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.debug.BezierDebugger;
import util.vector.Vector3;

import java.awt.*;

public class Normal1sV1 extends PanBot {

    private CarDestination desiredDestination;

    private SkillController dribbleController;
    private SkillController flickController;
    private SkillController driveToDestinationController;

    private PathHandler pathHandler;
    private PathHandler enemyNetPositionPath;
    private PathHandler ballPositionPath;

    private PidController playerPossessionPid;

    public Normal1sV1() {
        desiredDestination = new CarDestination();

        dribbleController = new Dribble(desiredDestination, this);
        flickController = new Flick(desiredDestination, this);
        driveToDestinationController = new DriveToDestination(desiredDestination, this);

        enemyNetPositionPath = new EnemyNetPositionPath(desiredDestination);
        ballPositionPath = new BallPositionPath(desiredDestination);
        pathHandler = ballPositionPath;

        playerPossessionPid = new PidController(1, 0, 12);
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        int playerIndex = input.playerIndex;
        int opponentIndex = (input.allCars.size()-1)-input.playerIndex;
        double playerPossessionRatio = PossessionEvaluator.possessionRatio(playerIndex, opponentIndex, input);
        double predictivePlayerPossessionRatio = playerPossessionPid.process(playerPossessionRatio, 0);
        // calculate next desired destination
        pathHandler.updateDestination(input);

        // do the thing

        // is it the kickoff...?
        if(input.ball.velocity.magnitude() < 0.1) {
            // destination on getNativeBallPrediction
            pathHandler = ballPositionPath;

            // drive to it
            driveToDestinationController.setupAndUpdateOutputs(input);
        }
        else {
            // destination on enemy net
            pathHandler = enemyNetPositionPath;
            System.out.println("bot predictive possession ratio: " + predictivePlayerPossessionRatio);

            // simply dribble and refuel if no threat
            if (predictivePlayerPossessionRatio > 600) {
                dribbleController.setupAndUpdateOutputs(input);
            }
            // flick the getNativeBallPrediction if threat
            else {
                flickController.setupAndUpdateOutputs(input);
            }
        }

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        Vector3 playerPosition = input.car.position;
        Vector3 destination = desiredDestination.getThrottleDestination();
        Vector3 steeringPosition = desiredDestination.getSteeringDestination();

        dribbleController.debug(renderer, input);

        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.LIGHT_GRAY, playerPosition, destination);
        renderer.drawLine3d(Color.MAGENTA, playerPosition, steeringPosition);
        BezierDebugger.renderPath(desiredDestination.getPath(), Color.blue, renderer);
    }
}
