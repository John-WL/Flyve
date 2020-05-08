package util.game_situation.handlers;

import util.game_situation.GameSituation;
import util.game_situation.UnhandledGameState;

import java.util.ArrayList;
import java.util.List;

public abstract class GameSituationHandler {

    private List<GameSituation> gameSituationList;
    private GameSituation currentGameSituation;
    private int currentGameSituationIndex;

    GameSituationHandler() {
        gameSituationList = new ArrayList<>();
        currentGameSituation = new UnhandledGameState();
        currentGameSituationIndex = 0;
    }

    public void add(GameSituation gameSituation) {
        gameSituationList.add(gameSituation);
    }

    public void update() {
        currentGameSituation.frameHappend();
        if(currentGameSituation.isGameStateElapsed()) {
            if(hasNext()) {
                next();
                currentGameSituation.reloadGameState();
            }
        }
    }

    public void reset() {
        currentGameSituationIndex = 0;
    }

    GameSituation next() {
        int index = currentGameSituationIndex;
        currentGameSituation = gameSituationList.get(index);
        currentGameSituationIndex++;
        return currentGameSituation;
    }

    abstract boolean hasNext();

    public abstract boolean hasBeenCompleted();

    int getNextGameSituationIndex() {
        return currentGameSituationIndex;
    }

    List<GameSituation> getGameSituationList() {
        return gameSituationList;
    }
}
