package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.elementary.general_driving.DriveToPredictedBallBounceController;
import rlbotexample.input.dynamic_data.DataPacket;
import util.math.vector.Vector3;

public class Dribble3 extends SkillController {

    private BotBehaviour bot;
    private Dribble2 dribbleController;
    private DriveToPredictedBallBounceController driveToPredictedBallBounceController;
    private Vector3 ballDestination;
    private Vector3 intermediateBallDestination;
    private int indexOfPlayerToAvoid;

    public Dribble3(BotBehaviour bot) {
        this.bot = bot;
        this.dribbleController = new Dribble2(bot);
        this.driveToPredictedBallBounceController = new DriveToPredictedBallBounceController(bot);
        this.intermediateBallDestination = new Vector3();
        this.indexOfPlayerToAvoid = -1;
    }

    public void setBallDestination(Vector3 ballDestination) {
        dribbleController.setBallDestination(ballDestination);
        driveToPredictedBallBounceController.setDestination(ballDestination);
    }

    public void setBallSpeed(double speed) {
        dribbleController.setBallSpeed(speed);
    }

    @Override
    public void updateOutput(DataPacket input) {
        if(canDribble(input)) {
            dribbleController.updateOutput(input);
        }
        else {
            driveToPredictedBallBounceController.updateOutput(input);
        }
    }

    private boolean canDribble(DataPacket input) {
        return input.car.position.minus(input.ball.position).magnitude() < 180;
    }

    @Override
    public void setupController() {

    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        if(canDribble(input)) {
            dribbleController.debug(renderer, input);
        }
        else {

        }
    }
}
