package rlbotexample.game_state;

import rlbot.cppinterop.RLBotDll;
import rlbot.flat.GameTickPacket;
import rlbot.gamestate.*;

public class GameState
{
    public static final double FIELD_SIZE_Y = 5200;

    public static void restartGameState()
    {
        float randomizedCarPositionX = 0*((float)(Math.random()*1000)-500);
        float randomizedCarPositionY = 0*((float)(Math.random()*2500));
        float randomizedCarRotation  = 0*((float)(Math.random()*2*Math.PI) - (float)Math.PI);

        rlbot.gamestate.GameState gameState = new rlbot.gamestate.GameState();
        gameState.withBallState(new BallState(new PhysicsState()
                .withLocation(new DesiredVector3(0f, 0f, 93f))
                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                .withRotation(new DesiredRotation(0f, 0f, 0f))
                .withVelocity(new DesiredVector3(0f, 0f, 0f))))
                .withCarState(0, new CarState()
                        .withPhysics(new PhysicsState()
                                .withRotation(new DesiredRotation(0f, (float)Math.PI/2 + randomizedCarRotation, 0f))
                                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                                .withLocation(new DesiredVector3(randomizedCarPositionX, -5000f + randomizedCarPositionY, 0f))
                                .withVelocity(new DesiredVector3(0f, 0f, 0f)))
                        .withBoostAmount(34f))
                .withCarState(1, new CarState()
                        .withPhysics(new PhysicsState()
                                .withRotation(new DesiredRotation(0f, (float)-Math.PI/2 + randomizedCarRotation, 0f))
                                .withAngularVelocity(new DesiredVector3(0f, 0f, 0f))
                                .withLocation(new DesiredVector3(-randomizedCarPositionX, 5000f - randomizedCarPositionY, 0f))
                                .withVelocity(new DesiredVector3(0f, 0f, 0f)))
                        .withBoostAmount(34f));

        RLBotDll.setGameState(gameState.buildPacket());
    }

    public static void restartGameOnGoalEvent()
    {
        // If a goal has just been made on this frame, restart the game state.
        if(GoalHandler.goalScoreEvent()) {
            GameState.restartGameState();
        }
    }

    public static int getCurrentGoalsDifference(GameTickPacket packet) {
        return packet.teams(0).score() - packet.teams(1).score();
    }
}
