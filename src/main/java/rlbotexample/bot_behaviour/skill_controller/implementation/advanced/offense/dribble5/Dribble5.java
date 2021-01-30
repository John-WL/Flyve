package rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5;

import rlbot.render.Renderer;
import rlbotexample.bot_behaviour.flyve.BotBehaviour;
import rlbotexample.bot_behaviour.skill_controller.SkillController;
import rlbotexample.bot_behaviour.skill_controller.implementation.advanced.offense.dribble5.dribble_recovery.DribbleRecoveryState;
import rlbotexample.input.dynamic_data.DataPacket;
import util.game_constants.RlConstants;
import util.math.vector.Vector3;
import util.state_machine.State;
import util.state_machine.StateMachine;

public class Dribble5 extends SkillController {

    private final BotBehaviour bot;

    private final StateMachine dribbleMachine;

    private double throttleAmount;
    private double steerAmount;

    public Dribble5(BotBehaviour bot) {
        this.bot = bot;
        State dribbleRecoveryState = new DribbleRecoveryState(bot);
        this.dribbleMachine = new StateMachine(dribbleRecoveryState);

        this.throttleAmount = 0;
        this.steerAmount = 0;
    }

    public void throttle(double throttleAmount) {
        this.throttleAmount = throttleAmount;
    }

    public void steer(double steerAmount) {
        this.steerAmount = steerAmount;
    }

    @Override
    public void updateOutput(DataPacket input) {
        dribbleMachine.exec(input);
    }

    private boolean botInControl(DataPacket input) {
        Vector3 lowestPointOfBall = input.ball.position
                .plus(Vector3.DOWN_VECTOR.scaled(RlConstants.BALL_RADIUS));
        Vector3 highestPointOnRoofOfCarHitBox = input.car.hitBox
                .closestPointOnSurface(input.car.position.plus(input.car.orientation.roofVector.scaled(100)));

        return lowestPointOfBall.minus(highestPointOnRoofOfCarHitBox).magnitude() < 160
                && input.car.hasWheelContact;
    }

    @Override
    public void setupController() {
    }

    @Override
    public void debug(Renderer renderer, DataPacket input) {
        dribbleMachine.debug(input, renderer);
    }
}
