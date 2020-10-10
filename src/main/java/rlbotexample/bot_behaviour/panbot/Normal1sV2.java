package rlbotexample.bot_behaviour.panbot;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.skill_controller.*;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.boost_management.RefuelProximityBoost;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.defense.ShadowDefense;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense.Dribble;
import rlbotexample.bot_behaviour.metagame.possessions.PossessionEvaluator;
import rlbotexample.bot_behaviour.skill_controller.advanced_controller.offense.Flick;
import rlbotexample.input.dynamic_data.ExtendedCarData;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.debug.BezierDebugger;
import util.vector.Vector3;

import java.awt.*;

public class Normal1sV2 extends PanBot {

    private SkillController dribbleController;
    private SkillController flickController;
    private SkillController driveToDestinationController;
    private SkillController shadowDefenseController;
    private SkillController refuelProximityBoostController;
    private SkillController skillController;

    private PidController playerPossessionPid;

    private boolean isRefueling;

    public Normal1sV2() {
        shadowDefenseController = new ShadowDefense(this);
        refuelProximityBoostController = new RefuelProximityBoost(this);
        skillController = driveToDestinationController;

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
            // destination on getNativeBallPrediction

            // drive to it
            skillController = driveToDestinationController;
        }
        else {
            // simply dribble and refuel if no threat
            if (predictivePlayerPossessionRatio > 400) {

                skillController = dribbleController;
                //System.out.println("dribble");
            }
            // flick the getNativeBallPrediction if threat
            else {

                skillController = flickController;
            }


            // find the threatening player
            ExtendedCarData closestCarToBall = input.allCars.get(0);
            for(ExtendedCarData car: input.allCars) {
                if(closestCarToBall.position.minus(input.ball.position).magnitude() > car.position.minus(input.ball.position).magnitude()) {
                    closestCarToBall = car;
                }
            }
            if(closestCarToBall != input.car && closestCarToBall.position.minus(input.ball.position).magnitude() < 400 && input.car.position.minus(input.ball.position).magnitude() > 200) {
                skillController = shadowDefenseController;
                isRefueling = false;
                //System.out.println("shadowD");
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

        // dribbleController.debug(renderer, input);
        shadowDefenseController.debug(renderer, input);

        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
