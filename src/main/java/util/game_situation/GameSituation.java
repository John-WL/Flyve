package util.game_situation;

import rlbot.cppinterop.RLBotDll;
import rlbot.gamestate.GameState;
import util.timer.Timer;

public abstract class GameSituation {

    private Timer gameStateDuration;

    GameSituation(Timer gameStateDuration)  {
        this.gameStateDuration = gameStateDuration;
        this.gameStateDuration.start();
        this.loadGameState();
    }

    public void reloadGameState() {
        this.gameStateDuration.start();
        this.loadGameState();
    }

    abstract void loadGameState();

    public boolean isGameStateElapsed() {
        return gameStateDuration.isTimeElapsed();
    }

    GameState getCurrentGameState() {
        return new rlbot.gamestate.GameState();
    }

    void applyGameState(GameState gameState) {
        RLBotDll.setGameState(gameState.buildPacket());
    }
}
