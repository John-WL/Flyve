package rlbotexample.bot_behaviour.panbot.implementation.normal_1s;

import rlbot.flat.GameTickPacket;
import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.panbot.PanBot;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.metagame.possessions.PossessionEvaluator;
import rlbotexample.input.dynamic_data.DataPacket;
import rlbotexample.output.BotOutput;
import util.controllers.PidController;
import util.math.vector.Vector3;

public class Normal1sV1 extends PanBot {

    private SkillController dribbleController;
    private SkillController flickController;
    private SkillController driveToDestinationController;


    private PidController playerPossessionPid;

    public Normal1sV1() {
        playerPossessionPid = new PidController(1, 0, 12);
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

            // drive to it
            driveToDestinationController.setupAndUpdateOutputs(input);
        }
        else {
            // destination on enemy net
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

        dribbleController.debug(renderer, input);

        super.updateGui(renderer, input, currentFps, averageFps, botExecutionTime);
    }
}
