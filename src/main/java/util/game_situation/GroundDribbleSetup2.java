package util.game_situation;

import rlbot.gamestate.*;
import util.timer.FrameTimer;

public class GroundDribbleSetup2 extends GameSituation {

    public GroundDribbleSetup2() {
        super(new FrameTimer(20*30));
    }

    @Override
    public void loadGameState() {
        GameState gameState = getCurrentGameState();
        gameState.withBallState(new BallState(new PhysicsState().withLocation(new DesiredVector3(0f, 3980f, 120f))
                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                .withRotation(new DesiredRotation(0f, 0f, 0f))
                .withVelocity(new DesiredVector3(0f, -300f, 0f))));

        gameState.withCarState(0, new CarState()
                .withPhysics(new PhysicsState()
                        .withRotation(new DesiredRotation(0f, (float)-Math.PI/2, 0f))
                        .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                        .withLocation(new DesiredVector3(30f, 4000f, 0f))
                        .withVelocity(new DesiredVector3(0f, -300f, 0f)))
                .withBoostAmount(100f));

        applyGameState(gameState);
    }
}
