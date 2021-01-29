package rlbotexample.bot_behaviour.flyve.implementation.normal_1s;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.metagame.possessions.PossessionEvaluator;
import rlbotexample.bot_behaviour.flyve.FlyveBot;
import rlbotexample.bot_behaviour.skill_controller.*;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.aerials.setup.AerialSetupController2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.boost_management.RefuelProximityBoost;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.defense.ShadowDefense;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Dribble2;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Flick;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.Flip;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DriveToDestination;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DriveToPredictedBallBounceController;
import rlbotexample.bot_behaviour.skill_controller.implementation.kickoff.comit_to_ball.KickoffSpecializedOnBall;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.input.dynamic_data.car.ExtendedCarData;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;

import java.awt.*;
import java.util.ArrayList;

public class Normal1sV3 extends FlyveBot {

    private Dribble2 dribbleController;
    private Flick flickController;
    private Flip flipController;
    private DriveToDestination driveToDestinationController;
    private ShadowDefense shadowDefenseController;
    private RefuelProximityBoost refuelProximityBoostController;
    private KickoffSpecializedOnBall kickoffController;
    private DriveToPredictedBallBounceController driveToPredictedBallBounceController;
    private AerialSetupController2 aerialSetupController;
    private SkillController skillController;

    private PidController playerPossessionPid;

    private boolean isRefueling;
    private boolean isAerialing;
    private boolean isInCriticalPosition;
    private int kickoffCallCounter;

    private String controllerLabel;

    public Normal1sV3() {

        dribbleController = new Dribble2(this);
        flickController = new Flick(this);
        flipController = new Flip(this);
        driveToDestinationController = new DriveToDestination(this);
        shadowDefenseController = new ShadowDefense(this);
        refuelProximityBoostController = new RefuelProximityBoost(this);
        kickoffController = new KickoffSpecializedOnBall(this);
        driveToPredictedBallBounceController = new DriveToPredictedBallBounceController(this);
        aerialSetupController = new AerialSetupController2(this);
        skillController = driveToDestinationController;

        playerPossessionPid = new PidController(1, 0, 10);

        isRefueling = false;
        isAerialing = false;
        kickoffCallCounter = 0;

        controllerLabel = "kickoff";
    }

    // called every frame
    @Override
    public BotOutput processInput(DataPacket input, GameTickPacket packet) {
        int playerIndex = input.playerIndex;

        int opponentIndex = getOpponents(input).get(0).playerIndex;
        double playerPossessionRatio = PossessionEvaluator.possessionRatio(playerIndex, opponentIndex, input);
        double predictivePlayerPossessionRatio = playerPossessionPid.process(playerPossessionRatio, 0);
        final Vector3 allyNetPosition = new Vector3(0, -5200 * (input.team == 0 ? 1 : -1), 100);

        // do the thing

        // is it the kickoff...?
        if(input.ball.velocity.magnitude() < 0.1) {
            // drive to it
            skillController = kickoffController;
            controllerLabel = "kickoff";

            if(kickoffCallCounter < 15) {
                output().jump(false);
            }
            kickoffCallCounter++;
        }
        else {
            // reset kickoff counter
            kickoffCallCounter = 0;

            // if the ball is bouncing and we're not directly dribbling, we need to go get the next bounce
            if(input.car.position.minus(input.ball.position).magnitude() > 300 /*&& (Math.abs(input.ball.velocity.z) > 200 || input.ball.position.z > 160)*/) {
                driveToPredictedBallBounceController.setDestination(allyNetPosition.scaled(-1));
                skillController = driveToPredictedBallBounceController;
                controllerLabel = "driveToBounce";

                if(input.ball.position.minus(input.car.position).normalized().dotProduct(input.car.velocity) > 700 && input.ball.position.minus(input.car.position).magnitude() > 1200) {
                    flipController.setDestination(input.statePrediction.ballAtTime(input.car.position.minus(input.ball.position).magnitude()/input.car.velocity.minus(input.ball.velocity).magnitude()).position);
                    skillController = flipController;
                    controllerLabel = "flip";
                }
            }
            else {
                // simply dribble and refuel if no threat
                skillController = flickController;

                // flick the getNativeBallPrediction if threat
                if (input.statePrediction.timeOfCollisionBetweenCarAndBall(1-input.playerIndex) > 2) {
                    skillController = dribbleController;
                    controllerLabel = "dribble";
                    //System.out.println("dribble");
                }
                else {
                }

                if(input.car.position.minus(input.ball.position).magnitude() < 160 && input.car.position.minus(allyNetPosition.scaled(-1)).magnitude() < input.ball.velocity.magnitude()*3) {
                    skillController = flickController;
                    controllerLabel = "flick";
                }
            }


            // find the threatening player
            ExtendedCarData closestCarToBall = input.allCars.get(0);
            for(ExtendedCarData car: input.allCars) {
                if(closestCarToBall.position.minus(input.ball.position).magnitude() > car.position.minus(input.ball.position).magnitude()) {
                    closestCarToBall = car;
                }
            }
            if(closestCarToBall != input.car && closestCarToBall.position.minus(input.ball.position).magnitude() < 160 && closestCarToBall.position.minus(input.ball.position).z < -50 && input.car.position.minus(input.ball.position).magnitude() > 300) {
                if(input.allCars.size() <= 2) {
                    skillController = shadowDefenseController;
                    controllerLabel = "shadowDefense";
                    isRefueling = false;
                    //System.out.println("shadowD");
                }
                else {
                    driveToDestinationController.setDestination(input.ball.position);
                    driveToDestinationController.setSpeed(RlConstants.CAR_MAX_SPEED);
                    skillController = driveToDestinationController;
                    controllerLabel = "driveToDestination";
                    isRefueling = true;
                }
            }

            // aerial handling
            if(input.statePrediction.ballAtTime(0.5).position.z > 400 && input.statePrediction.ballAtTime(0.5).velocity.z > 0 && input.car.boost * 20 > input.statePrediction.ballAtTime(1).position.z && input.car.position.minus(input.ball.position).flatten().magnitude() < 1000) {
                isAerialing = true;
            }
            else if(input.ball.position.z < 150) {
                isAerialing = false;
            }
            if(isAerialing) {
                aerialSetupController.setBallDestination(new Vector3(0, 5500 * (input.team == 0 ? 1 : -1), 500));
                //skillController = aerialSetupController;
            }

            // defense or smth
            if(allyNetPosition.minus(input.car.position).dotProduct(input.car.position.minus(input.ball.position.plus(new Vector3(0, 100*(input.team == 0 ? 1 : -1), 0)))) < 0) {
                isInCriticalPosition = true;
                final Vector3 defenseDirection = allyNetPosition.minus(input.ball.position);
                driveToDestinationController.setDestination(allyNetPosition);
                driveToDestinationController.setSpeed(Math.max(input.ball.velocity.magnitude()*2, 1400));
                controllerLabel = "weirdDefense";
                /*
                if(Math.abs(input.car.position.y) > 4800) {
                    improvisedDriveToDestinationController.setDestination(new Vector3(0, -5400 * (input.team == 0 ? 1 : -1), 100));
                    improvisedDriveToDestinationController.setSpeed(2300);
                }*/
            }
            else if(allyNetPosition.minus(input.car.position).dotProduct(input.car.position.minus(input.statePrediction.ballAtTime(allyNetPosition.minus(input.car.position).magnitude()/20000).position)) > 0) {
                isInCriticalPosition = false;
            }
            if(isInCriticalPosition) {
                //skillController = improvisedDriveToDestinationController;
            }

            /*
            // obvious rush on the getNativeBallPrediction?
            Vector3 playerNetCenterPosition;
            if(input.team == 0) {
                playerNetCenterPosition = new Vector3(0, -5500, 50);
            }
            else {
                playerNetCenterPosition = new Vector3(0, 5500, 50);
            }
            if(input.getNativeBallPrediction.velocity.y * playerNetCenterPosition.y < 0) {

            }*/
        }

        // do something about it
        skillController.setupAndUpdateOutput(input);

        if(input.car.position.minus(input.ball.position).magnitude() < 300) {
            isRefueling = false;
        }

        if(isRefueling) {
            // refuels boost if there is a pad near by
            refuelProximityBoostController.setupAndUpdateOutput(input);
            controllerLabel = "refuelProximity";
            //System.out.println("refueling");
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

    private java.util.List<ExtendedCarData> getOpponents(DataPacket input) {
        java.util.List<ExtendedCarData> opponents = new ArrayList<>();

        for(int i = 0; i < input.allCars.size(); i++) {
            if(input.allCars.get(i).team != input.car.team) {
                opponents.add(input.allCars.get(i));
            }
        }

        return opponents;
    }

    @Override
    public void updateGui(Renderer renderer, DataPacket input, double currentFps, double averageFps, long botExecutionTime) {
        skillController.debug(renderer, input);
        renderer.drawString3d(controllerLabel, Color.YELLOW, input.car.position, 10, 10);

        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
