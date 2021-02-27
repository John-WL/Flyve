package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.demos;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.ground_dribble.Dribble3;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;

import java.awt.*;

public class DribbleAvoidingOpponent extends SkillController {

    private BotBehaviour bot;
    private Dribble3 dribbleController;
    private Vector3 ballDestination;
    private Vector3 intermediateBallDestination;
    private int indexOfPlayerToAvoid;

    public DribbleAvoidingOpponent(BotBehaviour bot) {
        this.bot = bot;
        this.dribbleController = new Dribble3(bot);
        this.ballDestination = new Vector3();
        this.intermediateBallDestination = new Vector3();
        this.indexOfPlayerToAvoid = -1;
    }

    public void setBallDestination(Vector3 ballDestination) {
        this.ballDestination = ballDestination;
    }

    public void setPlayerToAvoid(int playerIndex) {
        this.indexOfPlayerToAvoid = playerIndex;
    }

    @Override
    public void updateOutput(DataPacket input) {
        Vector3 baseForce = ballDestination.minus(input.ball.position).scaledToMagnitude(1);
        Vector3 opponentRepulsionDirection = input.ball.position.minus(input.allCars.get(indexOfPlayerToAvoid).position).scaled(1, 1,0).normalized();
        double radius = input.ball.position.minus(input.allCars.get(indexOfPlayerToAvoid).position)
                .plus(input.allCars.get(indexOfPlayerToAvoid).velocity.scaled(-1))
                .magnitude();
        double magneticRepulsionStrength = 1000000/(radius * radius);
        Vector3 repulsionForce = opponentRepulsionDirection.scaledToMagnitude(magneticRepulsionStrength);

        intermediateBallDestination = input.ball.position.plus(baseForce.plus(repulsionForce).scaled(1000));

        dribbleController.setBallDestination(intermediateBallDestination);
        dribbleController.setBallSpeed(1200);
        if(input.allCars.get(indexOfPlayerToAvoid).position.y - input.ball.position.y > 0) {
            //dribbleController.setBallSpeed(2000);
        }
        dribbleController.updateOutput(input);
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        //dribbleController.debug(renderer, input);
        renderer.drawLine3d(Color.CYAN, input.ball.position.toFlatVector(), intermediateBallDestination.toFlatVector());
    }
}
