package rlbotexample.bot_behaviour.panbot;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.*;
import rlbotexample.bot_behaviour.skill_controller.basic_controller.Dribble;
import rlbotexample.bot_behaviour.car_destination.CarDestination;
import rlbotexample.bot_behaviour.metagame.PossessionEvaluator;
import rlbotexample.bot_behaviour.path.BallPositionPath;
import rlbotexample.bot_behaviour.path.EnemyNetPositionPath;
import rlbotexample.bot_behaviour.path.PathHandler;
import rlbotexample.input.dynamic_data.CarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.debug.BezierDebugger;
import util.vector.Vector3;

import java.awt.*;

public class Normal1sV2 extends PanBot {

    private CarDestination desiredDestination;

    private SkillController dribbleController;
    private SkillController flickController;
    private SkillController driveToDestinationController;
    private SkillController shadowDefenseController;
    private SkillController refuelProximityBoostController;
    private SkillController skillController;

    private PathHandler pathHandler;
    private PathHandler enemyNetPositionPath;
    private PathHandler ballPositionPath;

    private PidController playerPossessionPid;

    private boolean isRefueling;

    public Normal1sV2() {
        desiredDestination = new CarDestination();

        dribbleController = new Dribble(desiredDestination, this);
        flickController = new Flick(desiredDestination, this);
        driveToDestinationController = new DriveToDestination(desiredDestination, this);
        shadowDefenseController = new ShadowDefense(this);
        refuelProximityBoostController = new RefuelProximityBoost(this);
        skillController = driveToDestinationController;

        enemyNetPositionPath = new EnemyNetPositionPath(desiredDestination);
        ballPositionPath = new BallPositionPath(desiredDestination);
        pathHandler = ballPositionPath;

        playerPossessionPid = new PidController(1, 0, 10);

        isRefueling = false;
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        int playerIndex = input.playerIndex;
        int opponentIndex = (input.allCars.size()-1)-input.playerIndex;
        double playerPossessionRatio = PossessionEvaluator.possessionRatio(playerIndex, opponentIndex, input);
        double predictivePlayerPossessionRatio = playerPossessionPid.process(playerPossessionRatio, 0);

        // do the thing

        // is it the kickoff...?
        if(input.ball.velocity.magnitude() < 0.1) {
            // destination on ball
            pathHandler = ballPositionPath;

            // drive to it
            skillController = driveToDestinationController;
        }
        else {
            // destination on enemy net
            pathHandler = enemyNetPositionPath;

            // simply dribble and refuel if no threat
            if (predictivePlayerPossessionRatio > 400) {
                // destination on enemy net
                pathHandler = enemyNetPositionPath;

                skillController = dribbleController;
                //System.out.println("dribble");
            }
            // flick the ball if threat
            else {
                // destination on enemy net
                pathHandler = enemyNetPositionPath;

                skillController = flickController;
            }


            // find the threatening player
            CarData closestCarToBall = input.allCars.get(0);
            for(CarData car: input.allCars) {
                if(closestCarToBall.position.minus(input.ball.position).magnitude() > car.position.minus(input.ball.position).magnitude()) {
                    closestCarToBall = car;
                }
            }
            if(closestCarToBall != input.car && closestCarToBall.position.minus(input.ball.position).magnitude() < 400) {
                skillController = shadowDefenseController;
                isRefueling = false;
                //System.out.println("shadowD");
            }

            /*
            // obvious rush on the ball?
            Vector3 playerNetCenterPosition;
            if(input.team == 0) {
                playerNetCenterPosition = new Vector3(0, -5500, 50);
            }
            else {
                playerNetCenterPosition = new Vector3(0, 5500, 50);
            }
            if(input.ball.velocity.y * playerNetCenterPosition.y < 0) {

            }*/
        }

        // calculate next desired destination
        pathHandler.updateDestination(input);

        // do something about it
        skillController.setupAndUpdateOutputs(input);

        if(input.car.position.minus(input.ball.position).magnitude() < 300) {
            isRefueling = false;
        }

        if(isRefueling) {
            // refuels boost if there is a pad near by
            refuelProximityBoostController.setupAndUpdateOutputs(input);
            System.out.println("refueling");
        }
        if(input.car.boost > 90) {
            isRefueling = false;
        }
        else if(input.car.boost < 10) {
            isRefueling = true;
        }

        // return the calculated bot output
        return super.output();
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        Vector3 playerPosition = input.car.position;
        Vector3 destination = desiredDestination.getThrottleDestination();
        Vector3 steeringPosition = desiredDestination.getSteeringDestination();

        // dribbleController.debug(renderer, input);
        shadowDefenseController.debug(renderer, input);

        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
        renderer.drawLine3d(Color.LIGHT_GRAY, playerPosition, destination);
        renderer.drawLine3d(Color.MAGENTA, playerPosition, steeringPosition);
        BezierDebugger.renderPath(desiredDestination.getPath(), Color.blue, renderer);
    }
}
