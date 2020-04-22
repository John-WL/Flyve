package rlbotexample.game_situation;

import rlbot.gamestate.*;
import util.timer.Timer;

public class UnhandledGameState extends GameSituation {

    public UnhandledGameState() {
        super(new Timer(0));
    }

    @Override
    void loadGameState() {}
}
